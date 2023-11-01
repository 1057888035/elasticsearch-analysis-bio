/**
 * IK 中文分词  版本 5.0
 * IK Analyzer release 5.0
 * 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 源代码由林良益(linliangyi2005@gmail.com)提供
 * 版权声明 2012，乌龙茶工作室
 * provided by Linliangyi and copyright 2012 by Oolong studio
 * 
 */
package org.lifesci.bio.elasticsearch.core;

import com.mysql.cj.util.StringUtils;
import org.lifesci.bio.elasticsearch.beanFactory.BioBeanFactory;
import org.lifesci.bio.elasticsearch.service.DictionaryService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 英文字符及阿拉伯数字子分词器
 */
class LetterSegmenter implements ISegmenter {

	//子分词器标签
	static final String SEGMENTER_NAME = "LETTER_SEGMENTER";
	//链接符号
	private static final char[] Letter_Connector = new char[]{'#', '&', '+', '-', '.', '@', '_'};

	private final static char[] PUNCTION = new char[]{' ','(',')','/','\'','`'};

	//数字符号
	private static final char[] Num_Connector = new char[]{',', '.'};

	/*
	 * 词元的开始位置，
	 * 同时作为子分词器状态标识
	 * 当start > -1 时，标识当前的分词器正在处理字符
	 */
	private int start;
	/*
	 * 记录词元结束位置
	 * end记录的是在词元中最后一个出现的Letter但非Sign_Connector的字符的位置
	 */
	private int end;

	/*
	 * 字母起始位置
	 */
	private int englishStart;

	/*
	 * 字母结束位置
	 */
	private int englishEnd;

	/*
	 * 阿拉伯数字起始位置
	 */
	private int arabicStart;

	/*
	 * 阿拉伯数字结束位置
	 */
	private int arabicEnd;


	private DictionaryService dictionaryService = BioBeanFactory.getDictionaryBean();

	private List<Integer> hit = new ArrayList<>();

	private Lexeme readyLexeme = null;

	LetterSegmenter() {
		Arrays.sort(Letter_Connector);
		Arrays.sort(Num_Connector);
		this.start = -1;
		this.end = -1;
		this.englishStart = -1;
		this.englishEnd = -1;
		this.arabicStart = -1;
		this.arabicEnd = -1;
	}


	/* (non-Javadoc)
	 * @see org.wltea.analyzer.core.ISegmenter#analyze(org.wltea.analyzer.core.AnalyzeContext)
	 */
	public void analyze(AnalyzeContext context) {
		boolean bufferLockFlag = false;
		bufferLockFlag = this.processEnglishLetter(context) || bufferLockFlag;
		//判断是否锁定缓冲区
		if (bufferLockFlag) {
			context.lockBuffer(SEGMENTER_NAME);
		} else {
			//对缓冲区解锁
			context.unlockBuffer(SEGMENTER_NAME);
		}
	}

	/* (non-Javadoc)
	 * @see org.wltea.analyzer.core.ISegmenter#reset()
	 */
	public void reset() {
		this.start = -1;
		this.end = -1;
		this.englishStart = -1;
		this.englishEnd = -1;
		this.arabicStart = -1;
		this.arabicEnd = -1;
	}

	/**
	 * 处理纯字母输出
	 * @param context
	 * @return
	 */
	private boolean processEnglishLetter(AnalyzeContext context) {
		boolean needLock = false;

		if (this.englishStart == -1) {//当前的分词器尚未开始处理英文字符
			if (
					CharacterUtil.CHAR_ENGLISH == context.getCurrentCharType() ||
					CharacterUtil.CHAR_ARABIC == context.getCurrentCharType() ||
					CharacterUtil.CHAR_ARABIC == context.getCurrentCharType() ||
					context.getSegmentBuff()[context.getCursor()] == 40 ||
					context.getSegmentBuff()[context.getCursor()] == 41
			) {
				//记录起始指针的位置,标明分词器进入处理状态
				this.englishStart = context.getCursor();
				this.englishEnd = this.englishStart;
			}
		} else {//当前的分词器正在处理英文字符
			char c = context.getSegmentBuff()[context.getCursor()];
			if (
					CharacterUtil.CHAR_ENGLISH == context.getCurrentCharType() || c != 32
			) {
				//记录当前指针位置为结束位置
				this.englishEnd = context.getCursor();
			} else {
				this.hit.add(this.englishEnd - this.englishStart + 1);
				String type = dictionaryService.isMe(context.getSegmentBuff(), this.englishStart, this.englishEnd - this.englishStart + 1);
				if (!StringUtils.isNullOrEmpty(type)) {
					//遇到非English字符,标记此Lexeme
					String[] typeSplit = type.split(":::");
					Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, this.englishEnd - this.englishStart + 1 - Integer.valueOf(typeSplit[1]), typeSplit[0]);
					readyLexeme = newLexeme;
				}else if (!dictionaryService.isLike(context.getSegmentBuff(), this.englishStart, this.englishEnd - this.englishStart + 1)) {
					// don`t like db print english token
					Lexeme newLexeme = null;
					if (readyLexeme != null) {
						context.addLexeme(readyLexeme);
						readyLexeme = null;
					}else {
						newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, hit.get(0), Lexeme.TYPE_ENGLISH)
						context.addLexeme(newLexeme);
					}
					if (hit.size() > 0) {
						englishStart = englishStart + hit.get(0);
						for (int i = 1; i < hit.size(); i++) {
							if (hit.get(i) - hit.get(i - 1) > 0) {
								String tempType = dictionaryService.isMe(context.getSegmentBuff(), this.englishStart + 1, hit.get(i) - hit.get(i - 1) - 1);
								if (null != tempType) {
									String[] typeSplit = tempType.split(":::");
									newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart + 1, hit.get(i) - hit.get(i - 1) - 1 - Integer.valueOf(typeSplit[1]), typeSplit[0]);
								} else {
									newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart + 1, hit.get(i) - hit.get(i - 1) - 1, Lexeme.TYPE_ENGLISH);
								}
								context.addLexeme(newLexeme);
							}
							englishStart = englishStart + hit.get(i) - hit.get(i-1);
						}
					}
					this.englishStart = -1;
					this.englishEnd = -1;
					this.hit = new ArrayList<>();
				}

			}
		}

		//判断缓冲区是否已经读完
		if (context.isBufferConsumed() && (this.englishStart != -1 && this.englishEnd != -1)) {
			//缓冲以读完，输出词元
			String type = dictionaryService.isMe(context.getSegmentBuff(), this.englishStart, this.englishEnd - this.englishStart + 1);
			Lexeme newLexeme = null;
			if (!StringUtils.isNullOrEmpty(type)) {
				String[] typeSplit = type.split(":::");
				newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, this.englishEnd - this.englishStart + 1 - Integer.valueOf(typeSplit[1]), typeSplit[0]);
				this.englishStart = -1;
				this.englishEnd = -1;
				context.addLexeme(newLexeme);
			} else if (hit.size() > 0) {
				if (readyLexeme != null) {
					context.addLexeme(readyLexeme);
					readyLexeme = null;
				}
				newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, hit.get(0), Lexeme.TYPE_ENGLISH);
				context.addLexeme(newLexeme);
				englishStart = englishStart + hit.get(0);
				for (int i = 1; i < hit.size(); i++) {
					if (hit.get(i) - hit.get(i - 1) > 0) {
						newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart + 1, hit.get(i) - hit.get(i - 1) - 1, Lexeme.TYPE_ENGLISH);
						context.addLexeme(newLexeme);
					}
					englishStart = englishStart + hit.get(i) - hit.get(i-1);
				}
				if (hit.size() > 1) {
					newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart + 1, this.englishEnd - this.englishStart, Lexeme.TYPE_ENGLISH);
					context.addLexeme(newLexeme);
				}
				if (englishEnd - englishStart > 1) {
					newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart + 1, this.englishEnd - this.englishStart, Lexeme.TYPE_ENGLISH);
					context.addLexeme(newLexeme);
				}
				this.englishStart = -1;
				this.englishEnd = -1;
				this.hit = new ArrayList<>();
			} else {
				newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, this.englishEnd - this.englishStart + 1, Lexeme.TYPE_ENGLISH);
				this.englishStart = -1;
				this.englishEnd = -1;
				context.addLexeme(newLexeme);
			}
		}

		//判断是否锁定缓冲区
		if (this.englishStart == -1 && this.englishEnd == -1) {
			//对缓冲区解锁
			needLock = false;
		} else {
			needLock = true;
		}
		return needLock;
	}

/*	*//**
	 * 只保留字典字母
	 * @param context
	 * @return
	 *//*
	private boolean processBioOnlyLetter(AnalyzeContext context) {
		boolean needLock = false;
		if (this.englishStart == -1) {//当前的分词器尚未开始处理英文字符
			if (
					CharacterUtil.CHAR_ENGLISH == context.getCurrentCharType() ||
							CharacterUtil.CHAR_ARABIC == context.getCurrentCharType() ||
							context.getSegmentBuff()[context.getCursor()] == 40 ||
							context.getSegmentBuff()[context.getCursor()] == 41
			) {
				//记录起始指针的位置,标明分词器进入处理状态
				this.englishStart = context.getCursor();
				this.englishEnd = this.englishStart;
			}
		} else {//当前的分词器正在处理英文字符
			char c = context.getSegmentBuff()[context.getCursor()];
			if (c != 32) {
				//记录当前指针位置为结束位置
				this.englishEnd = context.getCursor();
			} else {
				this.hit.add(this.englishEnd - this.englishStart + 1);
				String type = dictionaryService.isMe(context.getSegmentBuff(), this.englishStart, this.englishEnd - this.englishStart + 1);
				if (!StringUtils.isNullOrEmpty(type)) {
					//遇到非English字符,标记此Lexeme
					String[] typeSplit = type.split(":::");
					Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, this.englishEnd - this.englishStart + 1 - Integer.valueOf(typeSplit[1]), typeSplit[0]);
					readyLexeme = newLexeme;
				}
				if (!dictionaryService.isLike(context.getSegmentBuff(), this.englishStart, this.englishEnd - this.englishStart + 1) && !hit.isEmpty()) {
					if (null != readyLexeme) {
						context.addLexeme(readyLexeme);
						this.englishStart = -1;
						this.englishEnd = -1;
						this.hit = new ArrayList<>();
						readyLexeme = null;
					} else {
						if (hit.size() > 1) {
							Integer endStart = this.englishEnd - (hit.get(hit.size() - 1) - hit.get(hit.size() - 2) - 1) + 1;
							String tempType = dictionaryService.isMe(context.getSegmentBuff(), endStart, this.englishEnd - endStart + 1);
							if (!StringUtils.isNullOrEmpty(tempType)) {
								String[] typeSplit = tempType.split(":::");
								Lexeme newLexeme = new Lexeme(context.getBufferOffset(), endStart, this.englishEnd - endStart + 1 - Integer.valueOf(typeSplit[1]), typeSplit[0]);
								context.addLexeme(newLexeme);
								this.englishStart = -1;
								this.englishEnd = -1;
								this.hit = new ArrayList<>();
							} else if (dictionaryService.isLike(context.getSegmentBuff(), endStart, this.englishEnd - endStart + 1)) {
								this.englishStart = endStart;
								this.hit = new ArrayList<>();
								this.hit.add(this.englishEnd - endStart + 1);
							} else {
								this.englishStart = -1;
								this.englishEnd = -1;
								this.hit = new ArrayList<>();
							}
						} else {
							this.englishStart = -1;
							this.englishEnd = -1;
							this.hit = new ArrayList<>();
						}
					}
				}
			}
		}

		*//**
		 * 缓冲区读完了
		 *//*
		if (context.isBufferConsumed() && (this.englishStart != -1 && this.englishEnd != -1)) {
			String type = dictionaryService.isMe(context.getSegmentBuff(), this.englishStart, this.englishEnd - this.englishStart + 1);
			if (!StringUtils.isNullOrEmpty(type)) {
				String[] typeSplit = type.split(":::");
				Lexeme newLexeme = new Lexeme(context.getBufferOffset(), this.englishStart, this.englishEnd - this.englishStart + 1 - Integer.valueOf(typeSplit[1]), typeSplit[0]);
				this.englishStart = -1;
				this.englishEnd = -1;
				context.addLexeme(newLexeme);
			}
			if (null != readyLexeme) {
				context.addLexeme(readyLexeme);
			}
		}


		//判断是否锁定缓冲区
		if (this.englishStart == -1 && this.englishEnd == -1) {
			//对缓冲区解锁
			needLock = false;
		} else {
			needLock = true;
		}
		return needLock;
	}*/

}
