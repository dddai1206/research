public class GossipMessage{
    private String content;
    private Node pid;
    private double p_required;
    private int hop;
    public GossipMessage(Node pid,String content,double p_required){
	this.content = content;
	this.pid = pid;
 	this.p_required = p_required;
    }
    public GossipMessage(Node pid,String content,double p_required,int hop){
	this.content = content;
	this.pid = pid;
 	this.p_required = p_required;
	this.hop = hop;
	
    }
    public String getContent(){
	return this.content;
    }
    public void setContent(String content){
	this.content = content;
    }
    public Node getPid(){
	return this.pid;
    }
    public void setPid(Node pid){
	this.pid = pid;
    }
    public double getP_required(){
	return this.p_required;
    }
    public void setP_required(double p_required){
	this.p_required = p_required;
    }
    public void setHop(int hop){
	this.hop = hop;
    }
    public int getHop(){
	return this.hop;
    }
}
