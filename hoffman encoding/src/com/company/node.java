package com.company;

public class node {
    private int value = 0 ;
    private char letter ='\0';

    node left;
    node right;
    //left represents 0 and right is 1 (for decoding and establishing each letter binary value)

    public void CreateNode(int val,char single_char){
        this.letter = single_char;
        this.value = val;
    }

    public char getLetter(){
        return this.letter;
    }
    public int getValue(){
        return this.value;
    }


}
