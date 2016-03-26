package io.jpress.controller.admin;

import io.jpress.core.Jpress;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.model.ModelSorter;
import io.jpress.model.Taxonomy;
import io.jpress.template.Module;
import io.jpress.template.Module.TaxonomyType;

import java.util.List;

import com.jfinal.plugin.activerecord.Page;

/**
 * Generated by JPress.
 */
@UrlMapping(url = "/admin/taxonomy", viewPath = "/WEB-INF/admin/taxonomy")
public class _TaxonomyController extends BaseAdminController<Taxonomy> { 
	
	
	private String getContentModule(){
		String module = getPara("m") ;
		if(null == module || "".equals(module)){
			module = Module.ARTICLE;
		}
		return module;
	}
	
	private String getType(){
		return getPara("t") ;
	}
	
	
	public void index(){
		String moduleName = getContentModule();
		Module module = Jpress.currentTemplate().getModuleByName(moduleName);
		TaxonomyType type = module.getTaxonomyTypeByType(getType());
		
		List<Taxonomy> list = Taxonomy.DAO.findListByModuleAndTypeAsSort(moduleName, type.getName());
		
		setAttr("module", module);
		setAttr("type", type);
		setAttr("taxonomys", list);
		
		super.index();
	}
	
	@Override
	public void save() {
		super.save();
		renderAjaxResultForSuccess("ok");
	}
	
	@Override
	public Page<Taxonomy> onPageLoad(int pageNumber, int pageSize) {
		Page<Taxonomy> page =  mDao.doPaginate(pageNumber, pageSize, getContentModule(),getType());
		ModelSorter.sort(page.getList());
		return page;
	}

}
