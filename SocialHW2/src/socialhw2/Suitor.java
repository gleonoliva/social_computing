/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialhw2;

/**
 *
 * @author Haley
 */
public class Suitor {
     int[] preference_list;
    int betrothed_id = -1;
    
    public Suitor (int[] pref_list){
        this.preference_list = pref_list;
    }
    
    public int get_prefid_at(int prefid_to_check){
        for(int i = 0; i < this.preference_list.length; i++){
            if(this.preference_list[i] == prefid_to_check){
                return i;
            }
        }
        return -1;
    }
    
    public void update_betrothed_id(int new_id){
        this.betrothed_id = new_id;
    }
}
