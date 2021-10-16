package com.company;

public class tree {
    private int ID;//used to locate ti in the priority queue
    private node Nodex =new node();

    public void CreateTree(node Left,node Right,int id){
        //takes two nodes and make the base of the tree
        int sum = Left.getValue() + Right.getValue();
        Nodex.CreateNode(sum,'\0');//top of the tree is always empty char
        Nodex.left = Left;
        Nodex.right = Right;
        this.ID = id;
    }

    public node GetTree(){
        return this.Nodex;//returns the tree that was constructed
    }

    public int GetID(){
        return this.ID;
    }

}
