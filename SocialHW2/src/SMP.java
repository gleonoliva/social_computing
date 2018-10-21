/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package socialhw2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author Haley
 */
public class SMP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception{
         // TODO code application logic here
        File file = new File(args[0]); 
  
        BufferedReader br = new BufferedReader(new FileReader(file)); 
        String toggle = args[1];
        //System.out.println(toggle);
        
        
        int pairing_size = Integer.parseInt(br.readLine().trim());
        
        String st;
        int line_id = 0;
        Suitor[] suitor_list = new Suitor[pairing_size];
        Bae[] bae_list = new Bae[pairing_size];
        while ((st = br.readLine()) != null) {
            String[] splited = st.split("\\s+");
            int[] pref_list = new int[pairing_size];
            for(int i = 0; i < splited.length; i++){
                pref_list[i] = Integer.parseInt(splited[i]);
            }
            if(line_id < pairing_size){
                if(toggle.charAt(0) == 'm'){
                    suitor_list[line_id] = new Suitor(pref_list);
                }
                else{
                    bae_list[line_id] = new Bae(pref_list);
                }
            }
            else{
                if(toggle.charAt(0) == 'm'){
                    bae_list[line_id - pairing_size] = new Bae(pref_list);
                }
                else{
                    suitor_list[line_id - pairing_size] = new Suitor(pref_list);
                }
            }
            line_id++;
        }
        
        while(!check_for_completeness(suitor_list, bae_list)){
            int questioning_suitor = find_single_suitor(suitor_list);
            for(int i = 0; i < suitor_list[questioning_suitor].preference_list.length; i++){
                int pursued = suitor_list[questioning_suitor].preference_list[i];
                int pursued_array_index = pursued - 1;
                if(bae_list[pursued_array_index].betrothed_id == -1){
                    suitor_list[questioning_suitor].update_betrothed_id(pursued);
                    bae_list[pursued - 1].betrothed_id = questioning_suitor + 1;
                    break;
                }
                else if(bae_list[pursued_array_index].check_new_proposal(questioning_suitor + 1)){
                    suitor_list[bae_list[pursued_array_index].betrothed_id - 1].betrothed_id = -1;
                    bae_list[pursued_array_index].betrothed_id = questioning_suitor + 1;
                    suitor_list[questioning_suitor].betrothed_id = pursued;
                    break;
                }
            }
            /*for(int i = 0; i < pairing_size; i++){
                System.out.println("Suitor " + i + " betrothed id is " + suitor_list[i].betrothed_id);
                System.out.println("Bae " + i + " betrothed id is " + bae_list[i].betrothed_id);
            }*/
        }
        
        for(int i = 0; i < suitor_list.length; i++){
            System.out.println("(" + Integer.toString(i + 1) + "," + suitor_list[i].betrothed_id + ")");
        }
    }
    
    public static Boolean check_for_completeness(Suitor[] suitor_array, Bae[] bae_array){
        for(int i = 0; i < suitor_array.length + bae_array.length; i++){
            if(i < suitor_array.length){
                if(suitor_array[i].betrothed_id == -1){
                    return false;
                }
            }
            else{
                if(bae_array[i - suitor_array.length].betrothed_id == -1){
                    return false;
                }
            }
        }
        return true;
    }
    
    public static int find_single_suitor(Suitor[] suitor_array){
        for(int i = 0; i < suitor_array.length; i++){
            if(suitor_array[i].betrothed_id == -1){
                return i;
            }
        }
        return -1;
    }
    
}

class Suitor {
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

class Bae extends Suitor{
    
    public Bae(int[] pref_list) {
        super(pref_list);
    }
    
    public Boolean check_new_proposal(int new_proposal_id){
        for(int i = 0; i < this.preference_list.length; i++){
            if(this.betrothed_id == this.preference_list[i]){
                //if they reach old partner's id first the old partner is preferred
                return false;
            }
            if(new_proposal_id == this.preference_list[i]){
                return true;
            }
        }
        return false;
    }
}

