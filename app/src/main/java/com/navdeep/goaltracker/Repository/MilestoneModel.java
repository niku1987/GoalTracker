package com.navdeep.goaltracker.repository;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.navdeep.goaltracker.utility.GoalTrackerDatabaseConnection;
import com.navdeep.goaltracker.pojo.Milestone;
import com.navdeep.goaltracker.interfaces.MilestoneModelViewPresenter;
import com.navdeep.goaltracker.pojo.MilestoneImage;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

public class MilestoneModel implements MilestoneModelViewPresenter.MilestoneModel {
     private ArrayList<Milestone> milestones;
     private static MilestoneModel milestoneModel = null;
     private final GoalTrackerDatabaseConnection connection;
     private MilestoneModel(Context context){
        milestones = new ArrayList<>();
        connection = GoalTrackerDatabaseConnection.getConnection(context);
    }

    public static MilestoneModel getMilestoneModel() {
        if(milestoneModel==null){
            milestoneModel = new MilestoneModel(null);
        }
        return milestoneModel;
    }

    @Override
    public void insertMilestone(Milestone milestone, int goalId) {
      //  milestones.add(milestone);
       //Log.i("Milestone", "Milestone Inserted");
       // Log.i("Milestone goal", goalId+"");
        SQLiteDatabase goalTrackerDatabase = connection.openDatabase();
        String title = milestone.getTitle();
        String description = milestone.getDescription();
        String time = milestone.getTime();
        String timer = milestone.getTimer();
        ContentValues milestoneContentValues = new ContentValues();
        milestoneContentValues.put("GOAL_ID", goalId);
        milestoneContentValues.put("MILESTONE_TITLE", title);
        milestoneContentValues.put("MILESTONE_DESCRIPTION", description);
        milestoneContentValues.put("MILESTONE_TIME", time);
        milestoneContentValues.put("MILESTONE_TIMER", timer);
        goalTrackerDatabase.insert("MILESTONE", null, milestoneContentValues);
        //Toast.makeText(context, "Goal Inserted",Toast.LENGTH_SHORT).show();
        connection.closeDatabase();
    }

    @Override
    public ArrayList<Milestone> getMilestones(int goalId)
    {
        SQLiteDatabase goalTrackerDatabase = connection.openDatabase();
        Log.i("Goal id", goalId+"");
        milestones = new ArrayList<>();
        Cursor cursor = goalTrackerDatabase.query("MILESTONE",
                new String[]{"MILESTONE_ID","GOAL_ID","MILESTONE_TITLE", "MILESTONE_DESCRIPTION", "MILESTONE_TIME","MILESTONE_TIMER"},
               "GOAL_ID = ?" ,new String[]{String.valueOf(goalId)},null,null,null);

        while (cursor.moveToNext()){
            int milestoneId = cursor.getInt(0);
            int goal = cursor.getInt(1);
            String title = cursor.getString(2);
            String description = cursor.getString(3);
            String time = cursor.getString(4);
            String timer = cursor.getString(5);
            Log.i("Milestone", milestoneId+" "+goal+""+title);
            /* TODO
            *   Alter the table to incluse milestone count */
            milestones.add(new Milestone(milestoneId, goalId, description, time, title, timer));
            Log.i("Milestone From Database", goal+"");
        }

        cursor.close();
        connection.closeDatabase();
     return milestones;

    }

    @Override
    public void updateMilestone(Milestone milestone) {
    /*TODO*/
        SQLiteDatabase goalTrackerDatabase = connection.openDatabase();
        int milestoneId =  milestone.getMilestoneId();
        ContentValues milestoneValues = new ContentValues();
        milestoneValues.put("MILESTONE_TITLE", milestone.getTitle());
        milestoneValues.put("MILESTONE_DESCRIPTION", milestone.getDescription());
        milestoneValues.put("MILESTONE_TIMER", milestone.getTimer());
        Log.d("updatedTitle", milestone.getTitle());
        goalTrackerDatabase.update("MILESTONE",
                milestoneValues,
                "MILESTONE_ID = ?",
                new String[]{ String.valueOf(milestoneId)});
        connection.closeDatabase();
    }

    /*Todo
    *  Instead of storing BLOB for an image, store its URI
    **/
    @Override
    public void insertImage(int id, MilestoneImage image) {
        SQLiteDatabase goalTrackerDatabase = connection.openDatabase();
        ContentValues imageContentValues = new ContentValues();

        String imageURI = image.getImageUri();
        long imageTime =  image.getCalendar().getTimeInMillis();
        imageContentValues.put("MILESTONE_ID", id);
        imageContentValues.put("IMAGE_DATA", imageURI);
        imageContentValues.put("IMAGE_TIME", imageTime);
        goalTrackerDatabase.insert("IMAGE", null, imageContentValues);
        connection.closeDatabase();
    }

    @Override
    public ArrayList<MilestoneImage> getImages(int milestoneId) {
        SQLiteDatabase goalTrackerDatabase = connection.openDatabase();
        ArrayList<MilestoneImage> images = new ArrayList<>();
        Cursor cursor = goalTrackerDatabase.query("IMAGE",
                new String[]{"IMAGE_ID","MILESTONE_ID","IMAGE_DATA","IMAGE_TIME"},
                "MILESTONE_ID = ?" ,new String[]{String.valueOf(milestoneId)},null,null,null);

        while (cursor.moveToNext()){
            int imageId = cursor.getInt(0);
            int milestone_Id = cursor.getInt(1);
            String imageUri= cursor.getString(2);
            long imageTime = cursor.getLong(3);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(imageTime);
            images.add(new MilestoneImage(imageId, imageUri, calendar));
        }
        cursor.close();
        connection.closeDatabase();
        return images;
    }
    @Override
    public void deleteImages(ArrayList<MilestoneImage> images, int milestoneId) {
        SQLiteDatabase goalTrackerDatabase = connection.openDatabase();
        for(Iterator itr = images.iterator(); itr.hasNext();){
            MilestoneImage image = (MilestoneImage) itr.next();
                     goalTrackerDatabase.delete("IMAGE",
                    "MILESTONE_ID = ? and IMAGE_ID = ?",
                    new String[]{ String.valueOf(milestoneId), String.valueOf(image.getImageId())});
        }
        goalTrackerDatabase.close();
    }

}
