/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.jme3.cursors.plugins.JmeCursor;
import worldserver3d.WorldApplication;

/**
 *
 * @author rgudwin
 */
public class Cursor {
    public static JmeCursor arrow_alt(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/arrow_alt.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(22);
        cur.setyHotSpot(25);
        return(cur);
    }
    
    public static JmeCursor arrow(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/arrow.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(0);
        cur.setyHotSpot(31);
        return(cur);
    }
    
    public static JmeCursor cross(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("images/cross.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    }
    
    public static JmeCursor dot(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/dot.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(6);
        cur.setyHotSpot(26);
        return(cur);
    }
    
   public static JmeCursor ew(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/ew.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    } 
   
   public static JmeCursor hand(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/hand.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(7);
        cur.setyHotSpot(31);
        return(cur);
    }
   
   public static JmeCursor help(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/help.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(0);
        cur.setyHotSpot(31);
        return(cur);
    }
   
   public static JmeCursor move(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/move.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    }
   
   public static JmeCursor nesw(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/nesw.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    }
   
   public static JmeCursor north(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/north.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    }
   
   public static JmeCursor ns(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/ns.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    }
   
   public static JmeCursor nwse(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/nwse.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    }
   
   public static JmeCursor pen(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/pen.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(0);
        cur.setyHotSpot(31);
        return(cur);
    }
   
   public static JmeCursor text_cursor(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/text_cursor.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(16);
        cur.setyHotSpot(16);
        return(cur);
    }
   
   public static JmeCursor unavail(WorldApplication app) {
        JmeCursor cur = (JmeCursor) app.getAssetManager().loadAsset("cursors/unavail.ico");
        //System.out.println("Cursor: "+cur.getHeight()+" "+cur.getWidth()+" "+cur.getXHotSpot()+" "+cur.getYHotSpot());
        cur.setxHotSpot(8);
        cur.setyHotSpot(24);
        return(cur);
    }
    
}
