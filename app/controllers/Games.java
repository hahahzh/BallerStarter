package controllers;

import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import models.Game;
import play.data.binding.Binder;
import play.db.Model;
import play.db.jpa.Blob;
import play.exceptions.TemplateNotFoundException;
import play.mvc.With;
import utils.DateUtil;

@Check("admin")
@With(Secure.class)
public class Games extends CRUD {
	 public static void create() throws Exception {
	        ObjectType type = ObjectType.get(getControllerClass());
	        notFoundIfNull(type);
	        Constructor<?> constructor = type.entityClass.getDeclaredConstructor();
	        constructor.setAccessible(true);
	        Game object = (Game) constructor.newInstance();
	        Binder.bindBean(params.getRootParamNode(), "object", object);
	        if(params.get("object.startDate")!=null){
	        	object.startDate = DateUtil.reverse2Date(params.get("object.startDate")+":00");
	        }
	        if(params.get("object.endDate")!=null){
	        	object.endDate = DateUtil.reverse2Date(params.get("object.endDate")+":00");
	        }
	        Set<String> s =params.data.keySet();
	        Iterator<String> i = s.iterator();
	        while(i.hasNext()){
	        	String name = i.next();
	        	if(name.contains("_hiddenblob")){
	        		String[] tmpParam = params.get(name).split(",");
	        		if(tmpParam.length == 2 && !tmpParam[0].equals("null")){
	                	Blob tmp = new Blob();
	                	tmp.set(new FileInputStream(tmpParam[0]), tmpParam[1]);
	                	name = name.replace("_hiddenblob", "").replace("object.", "");
	                	object.getClass().getField(name).set(object, tmp);
	        		}
	        	}
	        }
	        validation.valid(object);
	        if (validation.hasErrors()) {
	            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
	            try {
	                render(request.controller.replace(".", "/") + "/blank.html", type, object);
	            } catch (TemplateNotFoundException e) {
	                render("CRUD/blank.html", type, object);
	            }
	        }
	        object._save();
	        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
	        if (params.get("_save") != null) {
	            redirect(request.controller + ".list");
	        }
	        if (params.get("_saveAndAddAnother") != null) {
	            redirect(request.controller + ".blank");
	        }
	        redirect(request.controller + ".show", object._key());
	    }
	 
	 public static void save(String id) throws Exception {
		 	ObjectType type = ObjectType.get(getControllerClass());
	        notFoundIfNull(type);
	        Game object = Game.findById(Long.parseLong(id));
	        notFoundIfNull(object);
	        Binder.bindBean(params.getRootParamNode(), "object", object);
	        if(params.get("object.startDate")!=null){
	        	object.startDate = DateUtil.reverse2Date(params.get("object.startDate")+":00");
	        }
	        if(params.get("object.endDate")!=null){
	        	object.endDate = DateUtil.reverse2Date(params.get("object.endDate")+":00");
	        }
	        validation.valid(object);
	        if (validation.hasErrors()) {
	            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
	            try {
	                render(request.controller.replace(".", "/") + "/show.html", type, object);
	            } catch (TemplateNotFoundException e) {
	                render("CRUD/show.html", type, object);
	            }
	        }
	        object._save();
	        flash.success(play.i18n.Messages.get("crud.saved", type.modelName));
	        if (params.get("_save") != null) {
	            redirect(request.controller + ".list");
	        }
	        redirect(request.controller + ".show", object._key());
	    }
}