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
public class wayPoint{ 
    public int number;
    public int x;
    public int y;
    public int penalty;
    
    public wayPoint(int n,int xx,int yy,int p)
    {
        this.number=n;
        this.x=xx;
        this.y=yy;
        this.penalty=p;
    } 
    
}
