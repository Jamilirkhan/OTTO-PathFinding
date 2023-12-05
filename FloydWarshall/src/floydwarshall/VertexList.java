/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package floydwarshall;

/**
 *
 * @author jamil
 */
import java.util.ArrayList;



public class VertexList {
    
         
    ArrayList vertices; //Waypoints
    
    public VertexList(){
        vertices= new ArrayList();
    }
    
    public void InputVertices(int V)
    {
        String line;
        String[] tmp;
        int x,y,p;
        
        try{
        vertices.add(new wayPoint(0,0,0,0)); //adding first waypoint
        
        //this loop inputs all waypoints and adds them in the list
        for(int i=1;i<=V;i++)
        {
            line=StdIn.readLine();
            tmp=line.split(" ");
            x=Integer.parseInt(tmp[0]);
            y=Integer.parseInt(tmp[1]);
            p=Integer.parseInt(tmp[2]);
            if(!(x>=1 && x<=99 && y>=1 && y<=99))
                throw new RuntimeException("Coordinates of Waypoints must be 1<=X,Y<=99 ");
            if(!(p >=1 && p<=100))
                throw new RuntimeException("Paneltiy of Waypoints must be 1<=P <=100 ");
            
            vertices.add(new wayPoint(i,x,y,p));
            
        }
        vertices.add(new wayPoint(V+1,100,100,0)); //adding last waypoint
        }
        catch(Exception e)
        {
            StdOut.printf("Exception -->  Problem in the input, please try again  ");
            
        }
    }
    
     public double getWeight(int from, int to)
    {
        // weight of each edge is as below:
        //travel time+waiting time at the destination waypoint+penalty of destination waypoint
        
        double w=0.0; // weight
        int diff;
        wayPoint intermediate;
        
        wayPoint From=(wayPoint)vertices.get(from);
        wayPoint To=(wayPoint)vertices.get(to);
        //this statement below calculates the distance between two waypoints
        w=Math.sqrt((To.x-From.x)*(To.x-From.x) + (To.y-From.y)*(To.y-From.y));        
        //distance divided by 2 below, gives the speed of moving OTTO 
        //between two waypoints. This speed contributes to the weight of the edge
        //in OTTO challenge
        w=w/2;  
        //the statement below adds 10 to represent the waiting time of OTTO
        w=w+10;
                
        diff=to-from;
        
        if(diff>1)
        {
            //loop, below, adds the penalty of destination waypoint to the edge or path, ending at that waypoint
            for(int i=1;i<diff;i++)
            {
                intermediate=(wayPoint)this.vertices.get(i+from);
                w=w+intermediate.penalty;
            }
        }
        
        return w;
    }
    
    
}
