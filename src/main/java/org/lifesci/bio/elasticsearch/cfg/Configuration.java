/**
 * 
 */
package org.lifesci.bio.elasticsearch.cfg;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.core.PathUtils;
import org.elasticsearch.env.Environment;
import org.lifesci.bio.elasticsearch.dic.Dictionary;
import org.lifesci.bio.elasticsearch.plugin.BioPlugin;

import java.io.File;
import java.nio.file.Path;

public class Configuration {

	private Environment environment;
	private Settings settings;

	//是否启用智能分词
	private  boolean useSmart;

	//是否启用远程词典加载
	private boolean enableRemoteDict=false;

	//是否启用小写处理
	private boolean enableLowercase=true;


	public Configuration(boolean use_smart, boolean enable_lowercase, boolean enable_remote_dict){
		this.useSmart = use_smart;
		this.enableLowercase = enable_lowercase;
		this.enableRemoteDict = enable_remote_dict;
		Dictionary.initial(this);
	}

	@Inject
	public Configuration(Environment env,Settings settings) {
		this.environment = env;
		this.settings=settings;

		this.useSmart = settings.get("use_smart", "false").equals("true");
		this.enableLowercase = settings.get("enable_lowercase", "true").equals("true");
		this.enableRemoteDict = settings.get("enable_remote_dict", "true").equals("true");

		Dictionary.initial(this);

	}

	public Path getConfigInPluginDir() {
		return PathUtils
				.get(new File(BioPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath())
						.getParent(), "config")
				.toAbsolutePath();
	}

	public boolean isUseSmart() {
		return useSmart;
	}

	public Configuration setUseSmart(boolean useSmart) {
		this.useSmart = useSmart;
		return this;
	}

	public Environment getEnvironment() {
		return environment;
	}

	public Settings getSettings() {
		return settings;
	}

	public boolean isEnableRemoteDict() {
		return enableRemoteDict;
	}

	public boolean isEnableLowercase() {
		return enableLowercase;
	}
}
