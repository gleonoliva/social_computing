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
public class Bae extends Suitor{
    
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
