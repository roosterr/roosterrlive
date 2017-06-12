package com.roosterr;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kunwar on 11/26/2016.
 */

public class Checklists {
    public String name;
    public String id;
    public String checklist_id;
    public boolean checked;


    public Checklists(String _id,String name) {
        this.name = name;
        this.id=_id;
    }
    public Checklists(String name, boolean checked) {
        this.name = name;
        this.checked = checked;
    }
    public Checklists(String _id,String name,boolean checked) {
        this.name = name;
        this.id=_id;
        this.checked=checked;
    }
    public Checklists(String name, boolean checked,String _checklist_id) {
        this.name = name;
        this.checked = checked;
        this.checklist_id=_checklist_id;
    }

    public Checklists(String _id,String name,String _checklist_id) {
        this.name = name;
        this.id=_id;
        this.checklist_id = _checklist_id;
    }

    public static List<Checklists> getChecklists(Context context) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor cursor = dbController.fetchChecklists();
        StringBuilder str = new StringBuilder();
        List<Checklists> checklists = new ArrayList();
        while (!cursor.isAfterLast()) {
            Integer _ID = Integer.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper._ID))));
            String checklist_name = cursor.getString(cursor.getColumnIndex(DBHelper.CHECKLIST_NAME));

            checklists.add(new Checklists(_ID.toString(), checklist_name));
            cursor.moveToNext();
        }
        return checklists;
    }

    public static String updateChecklistName(Context context,String id,String checklist_name){
        SQLController dbController = new SQLController(context);
        dbController.open();
        if(id.equals("-1")){
            String rec_id = dbController.createChecklist(checklist_name);
            return rec_id;
        }
        else {
            dbController.updateChecklistName(id, checklist_name);
            return id;
        }
    }

    public static ArrayList<Checklists> getChecklistItems(Context context,String checklist_id){
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor cursor = dbController.fetchChecklistItems(checklist_id);
        StringBuilder str = new StringBuilder();
        ArrayList<Checklists> checklists = new ArrayList();
        while (!cursor.isAfterLast()) {
            Integer _ID = Integer.valueOf(Integer.parseInt(cursor.getString(cursor.getColumnIndex(DBHelper._ID))));
            String item_name = cursor.getString(cursor.getColumnIndex(DBHelper.CHECKLIST_ITEM));
            checklists.add(new Checklists(_ID.toString(), item_name,checklist_id));
            cursor.moveToNext();
        }
        return checklists;
    }

    public static void saveChecklistItem(Context context,String item_id,String item_name,String checklist_id){
        SQLController dbController = new SQLController(context);
        dbController.open();
        if(item_id==null){
            if(checklist_id.equals("-1")){
                Toast.makeText(context, "Update Category First", Toast.LENGTH_LONG).show();
            }
            else {
                dbController.createChecklistItems(checklist_id, item_name);
            }
        }
        else
            dbController.updateChecklistItems(item_id,item_name);
    }

}
