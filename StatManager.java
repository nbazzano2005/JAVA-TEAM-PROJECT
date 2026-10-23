/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package visnov.studio;

/**
 * Author: Trevor Hoang
 * Course: CS-321 | Intro to Java
 * Date: 10/22/26
 * Program: StatManager
 * Purpose: This code is designed to be the overhead manager for all stats within the visual novel.
 * 
 * Inheritance Objective: Given that this VisNovel will only track player/user stats, 
 * other group mates should use this code to create objects using StatManager. 
 * 
 * ---------------------------------------
 *         [EXAMPLE USAGE CASE]
 * ---------------------------------------
 * StatManager playStats = new StatManager();
 */

public class StatManager {
    // Variable Declarations
    private int intelligencePoints;
    private int socialScore;
    private int staminaValue;
    
    // Constructors - Initializes all scores to zero by default. 
    public StatManager(){
        this.intelligencePoints = 0;
        this.socialScore = 0;
        this.staminaValue = 0;
    }
    
    // Getters for checks.
    public int getIntelPoints(){ // Quiz/Test/Exam Checks.
        return intelligencePoints;
    }

    public int getSocialScore(){ // Socialization Checks.
        return socialScore;
    }
    
    public int getStaminaValue(){ // Resting Checks.
        return staminaValue;
    }
    
    // Necessary Methods - Additions
    public void addIntelligence(int studyPoints){ // Adds for points, exams, tests.
        intelligencePoints = intelligencePoints + studyPoints;
    }
    
    public void addSocialScore(int socialPoints){ // Adds for interactions with others.
        socialScore = socialScore + socialPoints;
    }
    
    public void addRest(int sleepPoints){ // Adds stamina points for user to rest. 
        staminaValue = staminaValue + sleepPoints;
    }
    
    // Necessary Methods - Subtractions
    public void subIntelligence(int lazePenalty){ // Subtracts intelligence points for intellectual complacency.
        intelligencePoints = intelligencePoints - lazePenalty;
        if (intelligencePoints < 0){ // Intelligence CANNOT fall shorter than zero. 
            intelligencePoints = 0; 
        }
    }
    
    public void subStamina(int energyPenalty){ // Subtracts stamina points per activity.
        staminaValue = staminaValue - energyPenalty;
        if (staminaValue < 0){ // Stamina CANNOT fall shorter than zero. Possible area to add a pass-out feature. 
            staminaValue = 0; 
        }
    }   
}
