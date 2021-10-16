package com.company;

public class charObject {
    private int ID;//used to locate it in the priority queue
    private char character;
    private int frequency = 0;

    public void CreateNewCharacterObject(char letter,int id){
        this.character =letter;
        this.frequency++;
        this.ID = id;
    }

    //setter
    public void Addfrequency(){
        this.frequency++;
    }

    //getter
    public int GetID(){return this.ID;}
    public int Get_frequency(){
        return this.frequency;
    }
    public char Get_char(){
        return this.character;
    }
}
