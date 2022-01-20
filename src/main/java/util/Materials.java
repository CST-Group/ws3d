/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import java.util.HashMap;

/**
 *
 * @author gudwin
 */
public class Materials {
    static AssetManager am;
    static HashMap<String,Material> mpool;
    
    public static void setMPool(HashMap<String,Material> nmpool) {
        mpool = nmpool;
    }
    
    public static Material getMaterial(String name) {
        Material m = mpool.get(name);
        return(m);
    }
    
}
