package controllers;

import play.i18n.Lang;
import play.mvc.Controller;

public class Application extends Controller {

    public static void index() {
        render("CRUD/index.html");
    }

    public static void list() {
        render();
    }
    
    public static void setLang(String lang) {    
        
    	index();
    }
}