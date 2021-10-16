package com.company;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;
import java.io.FileWriter;
public class Main {

    private static String Phrase = "Hello this is test";//for testign will use this for now
    private static charObject[] charObjects = new charObject[1000];
    private static int Nextfree = 500;//just set is as middle
    static List<Integer> priorityQueue = new ArrayList<>();//used to store tree and char objects to determine which one to add next.
    private static tree[] trees = new tree[1000];
    private static int UniqueId = 0;
    private static int k = 0;//used to populate trees
    static int pointer = 0;
    static String[][] twoD_arr = new String[2000][2];

    public static void main(String[] args) throws IOException {
        //doesnt save look up table to file so it saved during runtime in ram unfortunately
        String user_imp1 = "myfile.txt";
        String user_imp2 = "compressedFile.txt";
        String user_imp3 = "decompressed_target_file.txt";
        System.out.println("compressing the following file: " + user_imp1);
        System.out.println("saving to the following file: " + user_imp2);
        System.out.println();
        System.out.println("Look Up Table: ");
        if(!read_and_populate_charobjects(user_imp1)){
            System.out.println("empty input file");
        }else {//make it so user can enter source file
            savetoFile();
            System.out.println("decompressed message:");
            System.out.println();
            decompress(user_imp3);
            System.out.println();
            System.out.println("space saved(including the lookup table): " + Integer.toString(calculate_saving()) + " Bits");
        }
    }

    public static int calculate_saving(){
        int x = 0;
        int total_frequency = 0;
        int y = Nextfree + 1;
        int total_bits =0;
        int bits_used_by_table = 0;
        while(charObjects[y]!=null) {
            x = 0;
            while (twoD_arr[x][0] != null) {
                if(twoD_arr[x][0].equals(String.valueOf(charObjects[y].Get_char()))){
                    total_bits = total_bits + (twoD_arr[x][1].length()*charObjects[y].Get_frequency());
                    bits_used_by_table = bits_used_by_table + (8 + twoD_arr[x][1].length());//as there are 8 bits to represent a char
                    //AMOUNT OF BITS PER CHARACTER TIMES THE NUMBER OF CHARACTERS IN THE TEXT MESSAGE
                    break;
                }
                x++;
            }
            total_frequency = total_frequency + charObjects[y].Get_frequency();
            y++;
        }
        return (((total_frequency))*8) - total_bits - bits_used_by_table;
    }

    public static boolean read_and_populate_charobjects(String file1) throws IOException {
        Scanner scanner;
        try {
            File file = new File(file1); // java.io.File(source file with text)
            scanner = new Scanner(file);     // java.util.Scanner
            while (scanner.hasNextLine()) {//this loop is going to be used to read a text file (each line)
                //also implement sorting in the loop to save computation time
                int x = 0;
                Phrase = scanner.nextLine();
                while (x != Phrase.length()) {
                    int y = Nextfree + 1;
                    boolean found = false;
                    while (charObjects[y] != null) {
                        if (charObjects[y].Get_char() == Phrase.charAt(x)) {
                            charObjects[y].Addfrequency();//sees a existing one so adds +1 to frequency of that particular character.
                            //swap with the item on its right unless its equal number of frequency as the item to its right
                            if(charObjects[y + 1] !=null){//in case the character found is indeed the biggest and last character in the Array
                                if (charObjects[y].Get_frequency() > charObjects[y + 1].Get_frequency()) {
                                    charObject char_temp = charObjects[y + 1];//this might not work
                                    charObjects[y + 1] = charObjects[y];
                                    charObjects[y] = char_temp;
                                }
                            }
                            found = true;
                            break;
                        }
                        y++;
                    }
                    if (!found) {
                        charObjects[Nextfree] = new charObject();
                        charObjects[Nextfree].CreateNewCharacterObject(Phrase.charAt(x),UniqueId);
                        UniqueId++;//each object has to have a unique id or else the priority queue wont work
                        Nextfree = Nextfree - 1;//goes back by one
                    }
                    x++;
                }
            }
        if(Nextfree == 499){//validate if only contains one type of character
            System.out.println(charObjects[Nextfree+1].Get_char() + " | " + "0");
            twoD_arr[pointer][0] = String.valueOf(charObjects[Nextfree+1].Get_char());
            twoD_arr[pointer][1] = "0";

        }else{
            if(file.length() != 0) {
                CreateHoffman();
            }else{
                return false;
            }
        }
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        return true;
    }

    public static void AddToPriorityQueue(int ID){//sorts in order of size
        priorityQueue.add(ID);
        int x = 0;
        int swapCount = 0;
        int CurrentswapCount = 0;
        while(true){
            boolean reached_last = false;
            while (x < priorityQueue.size()+1){
                int[] size = new int[2];
                int q = 0;
                while(q<2) {//this loop grabs two items to compare them together
                    if(x+q>priorityQueue.size()-1){
                        reached_last = true;
                        break;
                    }
                    int CurrentId = priorityQueue.get(x + q);
                    boolean found = false;
                    while (!found) {
                        int y = Nextfree + 1;
                        while (charObjects[y] != null) {
                            if (CurrentId == charObjects[y].GetID()) {
                                size[q] = charObjects[y].Get_frequency();
                                found = true;
                                break;//found the id belonging to the object
                            }
                            y++;
                        }
                        int z = 0;
                        while (trees[z] != null) {
                            if (trees[z].GetID() == CurrentId) {
                                size[q] = trees[z].GetTree().getValue();
                                found = true;
                                break;//found the id belonging to the object
                            }
                            z++;
                        }
                    }
                    q++;
                }
                if(!reached_last) {
                    if (size[0] > size[1]) {//compare after since its could of been only item or last item
                        CurrentswapCount++;
                        Collections.swap(priorityQueue, x, x+1 );
                    }
                    x++;
                }else{
                    x=0;//reset x to prepare for another pass
                    if(CurrentswapCount == 0){
                        swapCount = 0;
                    }else{
                        swapCount = CurrentswapCount;
                        CurrentswapCount =0;
                    }
                    break;}
            }
            if(swapCount==0){
                break;
            }
        }
    }

     public static int GetandRemoveFromPriorityQueue(){//will have to check is priority queue is not empty,if it is then the process is finished
         int temp = priorityQueue.get(0);//gets the first item in the item queue
         priorityQueue.remove(0);//removes the first item in the priority queue
         return temp;
    }


    public static void CreateHoffman() throws IOException {
        int x = Nextfree + 1;//(will be re used for another loop)
        while (charObjects[x] != null) {//populate the priority queue with charObjects first.
            AddToPriorityQueue(charObjects[x].GetID());
            x++;
        }
        ;//this is the position of the smallest weighted character(based on its frequency)
        while (true) {//figure out termination of the loop(probably when the priority queue is empty of has one item?)
            int p = 0;

            tree temp_tree = null;
            charObject temp_charObject = null;

            tree temp_tree2 = null;
            charObject temp_charObject2 = null;

            while (p < 2) {//polls twice from the priority queue
                int CurrentId = GetandRemoveFromPriorityQueue();
                boolean found = false;
                while (!found) {
                    int y = Nextfree + 1;
                    while (charObjects[y] != null) {
                        if (CurrentId == charObjects[y].GetID()) {
                            if (p == 0) {
                                temp_charObject = charObjects[y];
                            } else {
                                temp_charObject2 = charObjects[y];
                            }
                            found = true;
                            break;//found the id belonging to the object
                        }
                        y++;
                    }
                    int z = 0;
                    while (trees[z] != null & !found) {
                        if (trees[z].GetID() == CurrentId) {
                            if (p == 0) {
                                temp_tree = trees[z];
                            } else {
                                temp_tree2 = trees[z];
                            }
                            found = true;
                            break;//found the id belonging to the object
                        }
                        z++;
                    }
                }
                p++;
            }
            //the above gives us either a char_object or tree given the id from the priority list.

            if (temp_charObject != null && temp_charObject2 != null) {//either has to be null so the other can't be null
                trees[k] = new tree();

                node temp_node = new node();
                temp_node.CreateNode(temp_charObject.Get_frequency(), temp_charObject.Get_char());

                node temp_node2 = new node();
                temp_node2.CreateNode(temp_charObject2.Get_frequency(), temp_charObject2.Get_char());

                trees[k].CreateTree(temp_node, temp_node2, UniqueId);
                AddToPriorityQueue(UniqueId);
                UniqueId++;
            } else if (temp_tree != null && temp_tree2 != null) {
                trees[k] = new tree();
                trees[k].CreateTree(temp_tree.GetTree(), temp_tree2.GetTree(), UniqueId);
                AddToPriorityQueue(UniqueId);
                UniqueId++;
            } else if (temp_tree != null && temp_charObject2 != null) {

                node temp_node2 = new node();
                temp_node2.CreateNode(temp_charObject2.Get_frequency(), temp_charObject2.Get_char());

                trees[k] = new tree();
                trees[k].CreateTree(temp_tree.GetTree(), temp_node2, UniqueId);
                AddToPriorityQueue(UniqueId);
                UniqueId++;
            } else if (temp_tree2 != null && temp_charObject != null) {

                node temp_node = new node();
                temp_node.CreateNode(temp_charObject.Get_frequency(), temp_charObject.Get_char());

                trees[k] = new tree();
                trees[k].CreateTree(temp_node, temp_tree2.GetTree(), UniqueId);
                AddToPriorityQueue(UniqueId);
                UniqueId++;
            }
            if (priorityQueue.size() == 1) {//if priority queue is 1 then it means the is only one big huffman tree made so the algo is finished
                break;
            }
            k++;
        }
        printCode(trees[k].GetTree(),"");
    }

    public static void printCode(node root, String s){
        if (root.left == null && root.right == null && root.getLetter() !='\0') {
            System.out.println(root.getLetter() + " | " + s);
                //creates a look up table (2-d array)
                twoD_arr[pointer][0] = String.valueOf(root.getLetter());
                twoD_arr[pointer][1] = s;
                pointer ++;
            return;
        }
        printCode(root.left, s + "0");
        printCode(root.right, s + "1");

    }

    public static void savetoFile() throws IOException {
        Scanner scanner;
        try {
            File file = new File("myfile.txt"); // java.io.File(source file with text)
            FileWriter myWriter = new FileWriter("compressedFile.txt");
            scanner = new Scanner(file);     // java.util.Scanner
            while (scanner.hasNextLine()) {//this loop is going to be used to read a text file (each line)
                Phrase = scanner.nextLine();
                int x = 0;
                StringBuilder encryptedline = new StringBuilder();
                while(x != Phrase.length()) {
                    int y= 0;
                    while(twoD_arr[y] != null){
                        if(String.valueOf(Phrase.charAt(x)).equals(twoD_arr[y][0])){
                            encryptedline.append(twoD_arr[y][1]);
                            break;
                        }
                        y++;
                    }
                    x++;
                }
                try {
                    myWriter.write(String.valueOf(encryptedline));
                } catch (IOException e) {
                    System.out.println("An error occurred while writing to file");
                    e.printStackTrace();
                }
            }
            myWriter.close();
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
    }

    public static void decompress(String file_to_send) throws IOException {
        Scanner scanner;
        String text = "";
        try {
            File file = new File("compressedFile.txt"); // java.io.File(source file with text)
            FileWriter myWriter = new FileWriter(file_to_send);
            scanner = new Scanner(file);     // java.util.Scanner
            while (scanner.hasNextLine()){//this loop is going to be used to read a text file (each line)
                Phrase = scanner.nextLine();
                int x = 0;
                StringBuilder binary = new StringBuilder();
                while(x != Phrase.length()) {
                    int y= 0;
                    binary.append(Phrase.charAt(x));
                    while(twoD_arr[y][1] != null){
                        if(twoD_arr[y][1].equals((binary).toString())){
                            binary.setLength(0);
                            text = text + twoD_arr[y][0];
                            break;
                        }
                        y++;
                    }
                    x++;
                }
            }
            myWriter.write(text);
            myWriter.close();
        } catch (Exception e) {
            System.out.println("error: " + e);
        }
        System.out.println(text);
    }
}
