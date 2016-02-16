public class BiconnectedGossipMessage extends GossipMessage{
    private int ttl;
    private Node neighbor;
    public BiconnectedGossipMessage(Node pid,String content,double p_required,int ttl,Node neighbor){
	super(pid,content,p_required);
	this.ttl = ttl;
	this.neighbor = neighbor;
    }

    public int getTtl(){
	return this.ttl;
    }
    public void setTtl(int ttl){
	this.ttl = ttl;
    }
    public Node getNeighbor(){
	return this.neighbor;
    }
    public void setNeighbor(Node neighbor){
	this.neighbor = neighbor;
    }

}
