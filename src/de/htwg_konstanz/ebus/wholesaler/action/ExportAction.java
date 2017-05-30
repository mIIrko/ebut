package de.htwg_konstanz.ebus.wholesaler.action;

import de.htwg_konstanz.ebus.wholesaler.demo.IAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

public class ExportAction implements IAction {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response, ArrayList<String> errorList) {
        return null;
    }

    @Override
    public boolean accepts(String actionName) {
        return false;
    }
}
