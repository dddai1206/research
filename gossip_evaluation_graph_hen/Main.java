import java.lang.Math;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayDeque;
import java.util.Random;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class Main{
    static int numOfMessage = 0;
    static int numOfBiconnectedMessage = 0;
    static int maxOfHop = 0;
    static int ffg_k = 2;
    static final int TTL = 2;
    static final long GOSSIP_SEED = 1009;
    static final long GRAPH_SEED = 1049;
    static final int GOSSIP_SELECT = 3;
    static final int INF = 1<<29;
    static final double D_P = 2.0;
    static final double R_P = 0.4;
    public static void main(String[] args){
	//link connect
	double area = 750*250;
	double r = 100;
	//ArrayList<Node> nodes = Main.initNodeRandomly(area,r);
	ArrayList<Node> nodes = Main.initNodeSkewed(area,r,D_P,R_P);
	Main.makeConnection(nodes,r);
	if(Main.checkConnectivity(nodes) == false){
	    Main.writeFile("result/connectivity.txt","not connect graph");
	    return;
	}else{
	    Main.writeFile("result/connectivity.txt","ok");
	}
	for(int i = 0;i < nodes.size();i++){
	    Main.searchBiconnected(nodes.get(i),Main.TTL);
	}
	//Main.floodingProtocol(nodes.get(0),nodes);
	int floodingNumOfMes = Main.numOfMessage;
	    
	//////////////////////////////
// 	System.out.println("digraph G{");
// 	System.out.println("size=\"100,100\"");
// 	System.out.println("layout=neato;");
// 	System.out.println("node[fontsize = 200,fixedsize = true,width = 5.0, height = 5.0];");
// 	Main.nodesDataStringOfDot(nodes);
// 	Main.linkStringOfDot(nodes);	
// 	//double reachability = Main.biconnectedGossipProtocol(nodes.get(0),nodes);
// 	System.out.println("}");
// 	if(true)
// 	    return ;
	//////////////////////////////////
	double reachability = 0;
	if(Main.GOSSIP_SELECT == 1){
	    reachability = Main.biconnectedGossipProtocol(nodes.get(0),nodes,Main.ffg_k);
	}else if(Main.GOSSIP_SELECT == 2){
	    reachability = Main.fixedFanoutGossipProtocol(nodes.get(0),nodes,Main.ffg_k);
	}else if(Main.GOSSIP_SELECT == 3){
	    reachability = Main.biconnectedProtocol(nodes.get(0),nodes);
	}
	
	//Main.stringOfDot(nodes);
	//Main.writeFile("result/res.txt",Main.D_P + " " + Main.R_P + " " + Main.numOfMessage + " " + reachability);
	System.out.println(Main.GOSSIP_SELECT + " " + Main.ffg_k + " " + Main.GRAPH_SEED + " " + Main.GOSSIP_SEED + " " + Main.D_P + " " + Main.R_P + " " + floodingNumOfMes + " " + Main.numOfMessage + " " + reachability + " " + Main.maxOfHop);
	return;
    }
    public static void writeFile(String fileName,String content){
	try{
	    File file = new File(fileName);
	    FileWriter filewriter = new FileWriter(file);
	    BufferedWriter bw = new BufferedWriter(filewriter);
	    PrintWriter pw = new PrintWriter(bw);
	    pw.println(content);
	    pw.close();
	    
	}catch(IOException e){
	    System.out.println(e);
	}

	 
    }
    public static void makeConnection(ArrayList<Node> nodes,double r){
	for(int i = 0;i < nodes.size();i++){
	    Node f = nodes.get(i);
	    for(int j = 0;j < nodes.size();j++){
		if(i == j)continue;
		Node s = nodes.get(j);
		if(Main.checkConnection(f,s,r)){
		    f.addLink(s);
		    s.addLink(f);
		    
		}
	    }
	}
    }
    public static ArrayList<Node> initNodeSkewed(double area,double r,double dp,double rp){
	double pai = 3.14;
	int numOfNode = (int)((1+1)*area*Math.log(area)/pai/r/r);
	int h = (int)(numOfNode/3.0);
	int numOfNodeInDp = (int)(h*dp);
	int numOfNodeInRp = (int)(h*rp);
	// System.out.println(numOfNode);
	// System.out.println(h);
	// System.out.println(numOfNodeInDp);
	// System.out.println(numOfNodeInRp);
	ArrayList<Node> nodes = new ArrayList<Node>(numOfNode);
	int cnt = 0;
	Random graphR = new Random(Main.GRAPH_SEED);
	for(int i = 0;i < 3;i++){
	    int nodeSize = 0;
	    if(i%2 == 0){
		nodeSize = numOfNodeInDp;
	    }else{
		nodeSize = numOfNodeInRp;
	    }
	    for(int j = 0;j < nodeSize;j++){
		Node node = new Node(cnt++);
		double area3 = 750/3.0;
		double x = area3 * (i%3) + graphR.nextDouble() * area3;
		double y = area3 * (int)(i/3) + graphR.nextDouble() * area3;
		node.setX(x);
		node.setY(y);
		nodes.add(node);
	    }
	}
	return nodes;
    }
    public static ArrayList<Node> initNodeRandomly(double area,double r){
	//__double area;// = 750*750;
	//uble r;// = 100;
	double pai = 3.14;
	int numOfNode = (int)((1+1)*area*Math.log(area)/pai/r/r);
	
	ArrayList<Node> nodes = new ArrayList<Node>(numOfNode);
	//node located in field randomly
	Random graphR = new Random(Main.GRAPH_SEED);
	for(int i = 0;i < numOfNode;i++){
	    Node node = new Node(i);
	    double x = graphR.nextDouble()*Math.sqrt(area);
	    double y = graphR.nextDouble()*Math.sqrt(area);
	    node.setX(x);
	    node.setY(y);
	    nodes.add(node);
	}
	return nodes;
    }
    public static double floodingProtocol(Node root,ArrayList<Node> nodes){
	Set<Node> usedNode = new TreeSet<Node>();
	ArrayDeque<Node> sendList = new ArrayDeque<Node>();
	ArrayDeque<GossipMessage> mesList = new ArrayDeque<GossipMessage>();
	sendList.push(root);
	mesList.push(new GossipMessage(root,"hoge",1.0));
	Main.numOfMessage = 0;
	while(!sendList.isEmpty()){
	    Node sender = sendList.pop();
	    GossipMessage mes = mesList.pop();
	    if(usedNode.contains(sender))continue;
	    usedNode.add(sender);
	    for(Node toNode:sender.getLink()){
		//if(toNode == mes.getPid())continue;
		sendList.push(toNode);
		mesList.push(new GossipMessage(sender,"hoge",1.0));
		Main.numOfMessage++;
	    }

	}
	//System.out.println("//reachability = " + (double)usedNode.size()/nodes.size());
	return (double)usedNode.size()/nodes.size();
    }
    public static double fixedFanoutGossipProtocol(Node root,ArrayList<Node> nodes,int p_k){
	Set<Node> usedNode = new TreeSet<Node>();
	ArrayDeque<Node> sendList = new ArrayDeque<Node>();
	ArrayDeque<GossipMessage> mesList = new ArrayDeque<GossipMessage>();
	sendList.add(root);
	mesList.add(new GossipMessage(null,"hoge",1.0,0));
	Random r = new Random(Main.GOSSIP_SEED);
	Main.numOfMessage = 0;
	int cnt = 0;
	ArrayList<Double> reachabilityByHop = new ArrayList<Double>(); 
	ArrayList<Integer> messageByHop = new ArrayList<Integer>();
	int keepHop = 0;
	while(!sendList.isEmpty()){
	    Node sender = sendList.poll();
	    sender.setFlag(true);
	    GossipMessage mes = mesList.poll();
	    if(keepHop == mes.getHop()){
		keepHop += 1;
		reachabilityByHop.add((double)usedNode.size()/(double)nodes.size());
		messageByHop.add(Main.numOfMessage);
	    }
	    if(usedNode.contains(sender))continue;
	    if(r.nextDouble() > mes.getP_required())continue;
	    usedNode.add(sender);
	    ArrayList<ArrayList<Integer> > biconnected = sender.getBiconnected();
	    Set<Node> keep = new TreeSet<Node>();
	    if(mes.getPid() == null){//ルートだけ
		for(Node senderNeighbor:sender.getLink()){
		    keep.add(senderNeighbor); 
		}
	    }
	    while(keep.size() < p_k){
		if(keep.size() >= sender.getLink().size() - 1){
		    break;
		}
		int ran_k = r.nextInt()%(sender.getLink().size());
		int tmp_cnt = 0;
		for(Node senderNeighbor:sender.getLink()){
		    if(tmp_cnt == ran_k && mes.getPid() != senderNeighbor && !keep.contains(senderNeighbor)){
			keep.add(senderNeighbor);
			break;
		    }
		    tmp_cnt++;
		}
	    }
	    	    
	    if(keep.size() != 0){
		cnt++;
	    }
	    for(Node toNode:keep){
		sendList.add(toNode);
		//System.out.println(sender.getId() + " -> " + toNode.getId() + "[color=\"red\",penwidth=\"8\",arrowsize = 8,fontsize = 180,label = \"" + cnt + "\"];");//ノードじょうほうを更新
		mesList.add(new GossipMessage(sender,"hoge",1.0,mes.getHop()+1));
		Main.maxOfHop = Math.max(mes.getHop()+1,Main.maxOfHop);
		Main.numOfMessage++;
	    }
	}
	reachabilityByHop.add((double)usedNode.size()/nodes.size());
	messageByHop.add(Main.numOfMessage);
	System.out.print(reachabilityByHop.size() + " ");
	for(int i = 0;i < reachabilityByHop.size();i++){
	    System.out.print(reachabilityByHop.get(i) + " " + messageByHop.get(i) + " ");
	}
	return (double)usedNode.size()/nodes.size();

    }
    public static double biconnectedGossipProtocol(Node root,ArrayList<Node> nodes,int p_k){
	Set<Node> usedNode = new TreeSet<Node>();
	ArrayDeque<Node> sendList = new ArrayDeque<Node>();
	ArrayDeque<GossipMessage> mesList = new ArrayDeque<GossipMessage>();
	sendList.add(root);
	mesList.add(new GossipMessage(null,"hoge",1.0,0));
	Random r = new Random(Main.GOSSIP_SEED);
	Main.numOfMessage = 0;
	int cnt = 0;
	ArrayList<Double> reachabilityByHop = new ArrayList<Double>(); 
	ArrayList<Integer> messageByHop = new ArrayList<Integer>();
	int keepHop = 0;
	while(!sendList.isEmpty()){
	    Node sender = sendList.poll();
	    sender.setFlag(true);
	    GossipMessage mes = mesList.poll();
	    if(keepHop == mes.getHop()){
		keepHop += 1;
		reachabilityByHop.add((double)usedNode.size()/(double)nodes.size());
		messageByHop.add(Main.numOfMessage);
	    }
	    if(usedNode.contains(sender))continue;
	    if(r.nextDouble() > mes.getP_required())continue;
	    usedNode.add(sender);
	    ArrayList<ArrayList<Integer> > biconnected = sender.getBiconnected();
	    Set<Node> keep = new TreeSet<Node>();
	    if(mes.getPid() == null){//ルートだけ
		for(Node senderNeighbor:sender.getLink()){
		    keep.add(senderNeighbor); 
		}
	    }else{
		ArrayList<Integer> list = biconnected.get(sender.getIdByNode(mes.getPid()));
		for(int i = 0;i < list.size();i++){
		    if(list.get(i) == Main.INF){
			keep.add(sender.getNodeById(i));
		    }
		}
	    }
	    int send_num = keep.size();
	    while(keep.size() < p_k + send_num){
		if(keep.size() >= sender.getLink().size() - 1){
		    break;
		}
		int ran_k = r.nextInt()%(sender.getLink().size());
		int tmp_cnt = 0;
		for(Node senderNeighbor:sender.getLink()){
		    if(tmp_cnt == ran_k && mes.getPid() != senderNeighbor && !keep.contains(senderNeighbor)){
			keep.add(senderNeighbor);
			break;
		    }
		    tmp_cnt++;
		}
	    }
	    if(keep.size() != 0){
		cnt++;
	    }
	    for(Node toNode:keep){
		sendList.add(toNode);
		//System.out.println(sender.getId() + " -> " + toNode.getId() + "[color=\"red\",penwidth=\"8\",arrowsize = 8,fontsize = 180,label = \"" + cnt + "\"];");//ノードじょうほうを更新
		mesList.add(new GossipMessage(sender,"hoge",1.0,mes.getHop()+1));
		Main.maxOfHop = Math.max(mes.getHop()+1,Main.maxOfHop);
		Main.numOfMessage++;
	    }
	}
	reachabilityByHop.add((double)usedNode.size()/nodes.size());
	messageByHop.add(Main.numOfMessage);
	System.out.print(reachabilityByHop.size() + " ");
	for(int i = 0;i < reachabilityByHop.size();i++){
	    System.out.print(reachabilityByHop.get(i) + " " + messageByHop.get(i) + " ");
	}
	return (double)usedNode.size()/nodes.size();
    }

    public static double biconnectedProtocol(Node root,ArrayList<Node> nodes){
	Set<Node> usedNode = new TreeSet<Node>();
	ArrayDeque<Node> sendList = new ArrayDeque<Node>();
	ArrayDeque<GossipMessage> mesList = new ArrayDeque<GossipMessage>();
	sendList.add(root);
	mesList.add(new GossipMessage(null,"hoge",1.0,0));
	Random r = new Random(Main.GOSSIP_SEED);
	Main.numOfMessage = 0;
	int cnt = 0;
	ArrayList<Double> reachabilityByHop = new ArrayList<Double>(); 
	ArrayList<Integer> messageByHop = new ArrayList<Integer>();
	int keepHop = 0;
	while(!sendList.isEmpty()){
	    Node sender = sendList.poll();
	    sender.setFlag(true);
	    GossipMessage mes = mesList.poll();
	    if(keepHop == mes.getHop()){
		keepHop += 1;
		reachabilityByHop.add((double)usedNode.size()/(double)nodes.size());
		messageByHop.add(Main.numOfMessage);
	    }
	    if(usedNode.contains(sender))continue;
	    if(r.nextDouble() > mes.getP_required())continue;
	    usedNode.add(sender);
	    ArrayList<ArrayList<Integer> > biconnected = sender.getBiconnected();
	    Set<Node> keep = new TreeSet<Node>();
	    if(mes.getPid() == null){//ルートだけ
		for(Node senderNeighbor:sender.getLink()){
		    keep.add(senderNeighbor); 
		}
	    }else{
		ArrayList<Integer> list = biconnected.get(sender.getIdByNode(mes.getPid()));
		for(int i = 0;i < list.size();i++){
		    if(2 <= list.get(i)){
			keep.add(sender.getNodeById(i));
		    }
		}
	    }
	    	    
	    if(keep.size() != 0){
		cnt++;
	    }
	    for(Node toNode:keep){
		sendList.add(toNode);
		//System.out.println(sender.getId() + " -> " + toNode.getId() + "[color=\"red\",penwidth=\"8\",arrowsize = 8,fontsize = 180,label = \"" + cnt + "\"];");//ノードじょうほうを更新
		mesList.add(new GossipMessage(sender,"hoge",1.0,mes.getHop()+1));
		Main.maxOfHop = Math.max(mes.getHop()+1,Main.maxOfHop);
		Main.numOfMessage++;
	    }
	}
	reachabilityByHop.add((double)usedNode.size()/nodes.size());
	messageByHop.add(Main.numOfMessage);
	System.out.print(reachabilityByHop.size() + " ");
	for(int i = 0;i < reachabilityByHop.size();i++){
	    System.out.print(reachabilityByHop.get(i) + " " + messageByHop.get(i) + " ");
	}
	return (double)usedNode.size()/nodes.size();
    }
    public static void searchBiconnected(Node root,int ttl){
	
	int linkSize = root.getLink().size();

	//init
	ArrayList<ArrayList<Integer> > biconnected = new ArrayList<ArrayList<Integer> >();
	for(int i = 0;i < linkSize;i++){
	    ArrayList<Integer> list = new ArrayList<Integer>(linkSize);
	    for(int j = 0;j < linkSize;j++){
		list.add(Main.INF);
	    }
	    biconnected.add(list);
	}
	int cnt = 0;
	for(Node node:root.getLink()){
	    root.addNodeToNum(node,cnt);
	    root.addNumToNode(cnt,node);
	    cnt++;

	}
	
	//search biconnected vertex
	for(Node node:root.getLink()){
	    ArrayDeque<Node> sendList = new ArrayDeque<Node>();
	    ArrayDeque<BiconnectedGossipMessage> mesList = new ArrayDeque<BiconnectedGossipMessage>();
	    Set<Node> usedList = new TreeSet<Node>();
	    sendList.push(node);
	    usedList.add(node);
	    mesList.push(new BiconnectedGossipMessage(root,"hoge",1.0,ttl,node));
	    while(!sendList.isEmpty()){
		Node sender = sendList.pop();
		BiconnectedGossipMessage mes = mesList.pop();
		int currentTtl = mes.getTtl();
		if(currentTtl == 0)continue;

		for(Node toNode:sender.getLink()){
		    if(toNode == root){
			biconnected.get(root.getIdByNode(node)).set(root.getIdByNode(sender),1);
			continue;
		    }else if(usedList.contains(toNode)){
			continue;
		    }
		    sendList.push(toNode);
		    mesList.push(new BiconnectedGossipMessage(mes.getPid(),mes.getContent(),mes.getP_required(),mes.getTtl()-1,mes.getNeighbor()));
		    Main.numOfBiconnectedMessage++;
		}
	    }
	}
	for(int i = 0;i < linkSize;i++){
	    for(int j = 0;j < linkSize;j++){
		for(int k = 0;k < linkSize;k++){
		    biconnected.get(j).set(k,Math.min(biconnected.get(j).get(k),biconnected.get(j).get(i) + biconnected.get(i).get(k)));
		}
	    }
	}
	root.setBiconnected(biconnected);
    }
    
    // public static void gossip(ArrayList<Node> nodes,int rootId){
    // 	Node root = nodes.get(rootId);
    // 	Node tmp = new Node(-1);
    // 	GossipMessage mes = new GossipMessage(tmp,"hogehoge",1.0);
    // 	ArrayList<Node> sendList = new ArrayList<Node>();
    // 	Set<Node> usedNodes = new TreeSet<Node>();
    // 	sendList.add(root);
    // 	while(sendList.size() > 0){
    // 	    Node sender = sendList.get(0);
    // 	    sendList.remove(0);
    // 	    //if(sender.containsGossipMessage(mes))continue;
    // 	    if(usedNodes.contains(sender))continue;
    // 	    usedNodes.add(sender);
    // 	    for(Node reciver:sender.getLink()){		
    // 		sender.sendGossipMessage(reciver,mes);
    // 		sendList.add(reciver);
    // 		Main.numOfMessage++;
    // 	    }
	     
    // 	}
	
    // }

    public static void stringOfDot(ArrayList<Node> nodes){
	System.out.println("digraph G{");
	System.out.println("size=\"100,100\"");
	System.out.println("layout=neato;");
	System.out.println("node[fontsize = 200,fixedsize = true,width = 5.0, height = 5.0];");
	Main.nodesDataStringOfDot(nodes);
	Main.linkStringOfDot(nodes);
	//Main.treeStringOfDot(nodes);
	System.out.println("}");
    }
    // public static void siblingStringOfDot(ArrayList<Node> nodes){
    // 	Set<Node> usedNodes = new TreeSet<Node>();
    // 	for(Node node:nodes){
    // 	    Set<Node> siblingSet = node.getSiblingSet();
    // 	    for(Node sibling:siblingSet){
    // 		if(usedNodes.contains(sibling))continue;
    // 		System.out.println(node.getId() + " -> " + sibling.getId() + "[color=\"blue\",penwidth=\"8\",arrowsize = 8];");
    // 	    }
    // 	    usedNodes.add(node);
    // 	}
    // }
    // public static void childStringOfDot(ArrayList<Node> nodes){
    // 	Set<Node> usedNodes = new TreeSet<Node>();
    // 	for(Node node:nodes){
    // 	    Set<Node> childSet = node.getChildSet();
    // 	    for(Node child:childSet){
    // 		if(usedNodes.contains(child))continue;
    // 		System.out.println(node.getId() + " -> " + child.getId() + "[color=\"blue\",penwidth=\"8\",arrowsize = 8];");
    // 	    }
    // 	    usedNodes.add(node);
    // 	}
    // }
    public static void nodesDataStringOfDot(ArrayList<Node> nodes){
	for(Node node:nodes){

	    if(node.getFlag()){
		System.out.println(node.getId() + " " + "[pos=\"" + node.getX() + "," + node.getY() + "!\"];");		
	    }else{
		System.out.println(node.getId() + " " + "[pos=\"" + node.getX() + "," + node.getY() + "!\", color = \"#336666\", fillcolor = \"#CC9999\"];");
	    }
	}
    }
    public static void treeStringOfDot(ArrayList<Node> nodes){
	for(Node node:nodes){
	    if(node.getPid() == null)continue;
	    System.out.println(node.getId() + " -> " + node.getPid().getId() + "[color=\"red\",penwidth=\"8\",arrowsize = 8];");
	}
    }
    public static void linkStringOfDot(ArrayList<Node> nodes){
	Set<Node> usedNode = new TreeSet<Node>();
	for(Node fromNode:nodes){
	    Set<Node> link = fromNode.getLink();
	    ArrayList<Node> toNodeList = new ArrayList<Node>();
	    for(Node toNode:link){
		if(usedNode.contains(toNode))continue;
		toNodeList.add(toNode);
	    }
	    for(Node toNode:toNodeList){
		System.out.println(fromNode.getId() + " -> " + toNode.getId() + " [dir = none];");
	    }
	    usedNode.add(fromNode);

	}
    }
    public static boolean checkConnectivity(ArrayList<Node> nodes){
	Set<Node> nodeSet = new TreeSet<Node>();
	ArrayList<Node> sendList = new ArrayList<Node>();
	sendList.add(nodes.get(0));
	while(sendList.size() > 0){
	    Node node = sendList.get(0);
	    sendList.remove(node);
	    if(nodeSet.contains(node)){
		continue;
	    }
	    nodeSet.add(node);
	    Set<Node> link = node.getLink();
	    for(Node nextNode:link){
		sendList.add(nextNode);
	    }
	    
	}
	return nodes.size() == nodeSet.size();
    }
    public static boolean checkConnection(Node f,Node s,double r){
	double x1 = f.getX();
	double y1 = f.getY();
	double x2 = s.getX();
	double y2 = s.getY();
	return r*r >= (x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2);
    }
}
