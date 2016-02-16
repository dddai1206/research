import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.Comparable;
public class Node implements Comparable<Node>{
    private int id;
    private Node pid;
    private Set<Node> link;
    private Set<GossipMessage> messageSet;
    private double x,y;
    private ArrayList<ArrayList<Integer> > biconnected;
    private HashMap<Node,Integer> nodeToNum;
    private HashMap<Integer,Node> numToNode;
    private boolean flag;
    public Node(int id){
	this.id = id;
	this.pid = null;
	this.link = new TreeSet<Node>();
	this.messageSet = new TreeSet<GossipMessage>();
	this.x = 0;
	this.y = 0;
	this.nodeToNum = new HashMap<Node,Integer>();
	this.numToNode = new HashMap<Integer,Node>();
	this.flag = false;
		    
    }
    public void setFlag(boolean flag){
	this.flag = flag;
    }
    public boolean getFlag(){
	return this.flag;
    }
    public HashMap<Integer,Node> getNumToNode(){
	return this.numToNode;
    }
    public void addNumToNode(int i,Node node){
	this.numToNode.put(i,node);
    }
    public Node getNodeById(int i){
	return this.numToNode.get(i);
    }
    public HashMap<Node,Integer> getNodeToNum(){
	return this.nodeToNum;
    }
    public void addNodeToNum(Node node,int i){
	this.nodeToNum.put(node,i);
    }
    public int getIdByNode(Node node){
	return this.nodeToNum.get(node);
    }

    public int getId(){
	return this.id;
    }
    public void setId(int id){
	this.id = id;
    }
    public Set<Node> getLink(){
	return this.link;
    }
    public Set<GossipMessage> getMessageSet(){
	return this.messageSet;
    }
    public ArrayList<ArrayList<Integer> > getBiconnected(){
	return this.biconnected;
    }
    public void setBiconnected(ArrayList<ArrayList<Integer> > biconnected){
	this.biconnected = biconnected;
    }
    public Node getPid(){
	return this.pid;
    }
    public void setPid(Node pid){
	this.pid = pid;
    }
    public double getX(){
	return this.x;
    }
    public void setX(double x){
	this.x = x;
    }
    public double getY(){
	return this.y;
    }
    public void setY(double y){
	this.y = y;
    }
    public void addLink(Node node){
	this.link.add(node);
    }
    public void addMessageSet(GossipMessage message){
	this.messageSet.add(message);
    }
    
    public void sendGossipMessage(Node toNode,GossipMessage gossipMessage){
	if(this.containsGossipMessage(gossipMessage))return;
	this.addMessageSet(gossipMessage);
	toNode.recievedGossipMessage(this,gossipMessage);
    }
    public void recievedGossipMessage(Node fromNode,GossipMessage gossipMessage){
	this.addMessageSet(gossipMessage);
    }
    
    public boolean containsGossipMessage(GossipMessage gossipMessage){
    	return this.messageSet.contains(gossipMessage);
    }
    public void nodeInfomation(){
	System.out.println("id = " + this.id);
	System.out.println("x = " + this.x);
	System.out.println("y = " + this.y);
    }
    public void printOutBiconnected(){
	for(int i = 0;i < this.biconnected.size();i++){
	    System.out.print(this.getNodeById(i).getId() + "\t");
	    for(int j = 0;j < this.biconnected.get(i).size();j++){
		if(biconnected.get(i).get(j) >= 10000){
		    System.out.print("INF" + "\t");
		}
		else {
		    System.out.print(biconnected.get(i).get(j) + "\t");
		}
		
	    }
	    System.out.println("");
	}
	   
    }
    
    public int compareTo(Node node){
	return this.getId() - node.getId();
    }
}
