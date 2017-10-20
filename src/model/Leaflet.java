/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Patricia Rocha de Toro, Elisa Calhau de Castro, Ricardo Ribeiro Gudwin
 *****************************************************************************/

package model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;
import util.Constants;

/**
 *
 * @author eccastro
 */
public class Leaflet {

    private Long ID;
    private int numberOfItems = 0;  
    private boolean ifCompleted;
    private int[] items;
    private int active = 1; //true: not delivered yet
    private int payment = 0; //number of points is gained by a creature when it is delivered
    private Long ownerID = new Long(-1); //all creatures are owner. There is only one leaflet pool
                                         // that is shared by all creatures
    //Type, (Total number, Collected number)
    //For each type of jewel, how many must be collected and how many have already been collected.
    private HashMap<String, ItemAttributes> itemsMap = new HashMap<String, ItemAttributes>();
    Logger log;

    public Leaflet() {
        ID = System.currentTimeMillis();
        ifCompleted = false;
        log = Logger.getLogger(Leaflet.class.getCanonicalName());
    }

    public Leaflet(int[] jewelTypes) {
        this();
        items = jewelTypes;
        buildItemsMapAndPayment(jewelTypes);
        numberOfItems = itemsMap.keySet().size();
        
        
    }

    public Leaflet(int[] jewelTypes, Long ownerID) {
        this(jewelTypes);
        this.ownerID = ownerID;
        
    }
    public Long getOwner(){
        return ownerID;
    }
    public int getNumberOfItemTypes(){
        return numberOfItems;
    }
    public int[] getItems() {

        return items;
    }

    public int getPayment(){
        return payment;
    }
    public synchronized int getActivity(){
        return active;
    }
    public synchronized void setActivity(int ac){
        active = ac;
    }
    
    public synchronized void resetLeaflet(){
        for(Map.Entry<String, ItemAttributes> jewel : itemsMap.entrySet()){
            jewel.getValue().collected = 0;
        }
    }

    /**
     * Return the number of crystals of a certain type (i.e. color).
     * @param type
     * @return
     */
    public int getTotalNumberOfType(String type){

        if(itemsMap.containsKey(type)){
            return itemsMap.get(type).totalNumber;
        }else return -1;
    }
    
     public boolean isIfCompleted() {
        return ifCompleted;
    }

    public void setIfCompleted(boolean ifCompleted) {
        this.ifCompleted = ifCompleted;
    }
    
    
    public boolean ifInLeaflet(String type) {
        return this.itemsMap.containsKey(type);
    }
    
    /**
     * retorna "true" se todos os itens do leaflet tiverem sido coletados.
     */
     
    public boolean ifCompleted() {
        int result = 0;
        int[] aux = this.getItems();
        for (int i = 0; i < aux.length; i++) {
            if (!this.ifAllCollected(Constants.getColorItem(aux[i]))) {
		result++;
	    }
        }
        return (result == 0);
    }
    
    /**
     * retorna "true" se todos os itens de um determinado tipo (type) tiverem
     * sido coletados; é um método auxiliar usado no método ifCompleted.
     * @param type
     * @return 
     */
    
    public boolean ifAllCollected (String type) {
        boolean result = false;
        ItemAttributes iA = this.itemsMap.get(type);
        if (iA.collected == iA.totalNumber) {
            result = true;
        }
        return result;
    }
    
    
     public void updateCollected(String type, boolean inc) {

        if (itemsMap.containsKey(type)) {  
                        
            if (inc) {
                itemsMap.get(type).incCollected();
            } else {
                itemsMap.get(type).decCollected();
            }
        }
        
        
    }
     
     
    

    public Integer getNumberOfJewels(String type){

        if(itemsMap.containsKey(type)){
            return itemsMap.get(type).totalNumber;
        }else return 0;
    }

    public boolean ifInLeaflet(int thing) {

        for (int i = 0; i <= (items.length - 1); i++) {
            if (items[i] == thing) {
                return true;
            }

        }

        return false;
    }
    public Long getID(){
        return ID;
    }

    private void buildItemsMapAndPayment(int[] jewelTypes){
        for (int i = 0; i <= (jewelTypes.length - 1); i++) {
            if(! itemsMap.containsKey(Constants.getColorItem(jewelTypes[i]))){
                //first entry of type
                itemsMap.put(Constants.getColorItem(jewelTypes[i]), new ItemAttributes(1));

            }else{
                int inc = 1+itemsMap.get(Constants.getColorItem(jewelTypes[i])).totalNumber;
                itemsMap.get(Constants.getColorItem(jewelTypes[i])).setTotalNumber(inc);
            }
            payment = payment + Constants.getColorPayment(jewelTypes[i]);
            
        }
    }
    /**
     * Format: ID(space)Color(space)number(space)
     * Begin and end with blank space
     * @return
     */
    @Override
    public String toString(){
        String ret = "LeafletID: "+this.ID+" ";
        for (Iterator<String> iter = itemsMap.keySet().iterator(); iter.hasNext();) {
            String str = iter.next(); //jewel color
            ret = ret + str + " ";
            ret = ret + itemsMap.get(str) + " ";
        }
        ret = ret+" payment= "+payment;
        return ret;

    }
    
    
    public void printLeafletSituation() {

        log.info("LeafletID: " + this.ID + ". ");
        int[] aux = this.getItems();

        for (int i = 0; i < aux.length; i++) {
            ItemAttributes iA = this.itemsMap.get(Constants.getColorItem(aux[i]));

            log.info("Itens: " + Constants.getColorItem(aux[i]) + "  " + iA.collected+" ; ");

        }
        log.info("");
        log.info("______________________________________________________");

    }

    
    
    
    
    /**
     * Formatted to send to server following the protocol
     * @return
     */
    public String toStringFormatted(){
        String ret = " ";
        for (Iterator<String> iter = itemsMap.keySet().iterator(); iter.hasNext();) {
            String str = iter.next(); //jewel color
            ret = ret + str + " ";
            ret = ret + (itemsMap.get(str)).toString() + " ";
        }
              
        ret  = ret+payment+" "+ifCompleted()+" ";
        return ret;

    }
    public class ItemAttributes {

        int totalNumber;
        int collected;
        

        
        public ItemAttributes(int tn) {
            totalNumber = tn;
            collected = 0;
           ;
        }
        public void setTotalNumber(int n){
            totalNumber = n;
        }

        public void setCollected(int c){
            collected = c;
        }
        public void incCollected(){
            collected++;
        }
        public void decCollected(){
            collected--;
        }
        public String toString(){
             return (" "+totalNumber+" "+collected);
        }
       

    }
}
