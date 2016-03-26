package io.jpress.model;

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseOption;

import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

@Table(tableName="option",primaryKey="id")
public class Option extends BaseOption<Option> {

	private static final long serialVersionUID = 1L;
	
	public static final Option DAO = new Option();
	
	public static final String KEY_WEB_NAME = "web_name";
	public static final String KEY_TEMPLATE_NAME = "template_name";
	
	public static String findTemplateName(){
		return cacheValue(KEY_TEMPLATE_NAME);
	}
	
	public static String findWebName(){
		return cacheValue(KEY_WEB_NAME);
	}
	
	
	
	public static void saveOrUpdate(String key,String value){
		Option option = findByKey(key);
		if(null == option){
			option = new Option();
		}
		
		option.setOptionKey(key);
		option.setOptionValue(value);
		
		CacheKit.remove(CACHE_NAME, key);
		
		option.saveOrUpdate();
	}
	
	
	public static String cacheValue(final String key){
		return CacheKit.get(CACHE_NAME, key, new IDataLoader() {
			@Override
			public Object load() {
				return findValue(key);
			}
		});
	}
	
	public static String findValue(String key){
		Option option = DAO.doFindFirst("option_key =  ?",key);
		if(null != option){
			return option.getOptionValue();
		}
		return null;
	}
	
	public static Boolean findValueAsBool(String key){
		Option option = DAO.doFindFirst("option_key =  ?",key);
		if(null != option){
			String value = option.getOptionValue();
			if(value != null){
				try {
					return Boolean.parseBoolean(value);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return null;
	}
	
	
	public static Integer findValueAsInteger(String key){
		Option option = DAO.doFindFirst("option_key =  ?",key);
		if(null != option){
			String value = option.getOptionValue();
			if(value != null){
				try {
					return Integer.parseInt(value);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return null;
	}
	
	
	public static Float findValueAsFloat(String key){
		Option option = DAO.doFindFirst("option_key =  ?",key);
		if(null != option){
			String value = option.getOptionValue();
			if(value != null){
				try {
					return Float.parseFloat(value);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return null;
	}
	
	public static Option findByKey(String key){
		return DAO.doFindFirst("option_key =  ?",key);
	}
	
	
}
