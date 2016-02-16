dp = 0
rp = 0
graph_seed = 1000
gossip_seed = 1000
gossip_select = 1;
graph_seed_max = 1050
gossip_seed_max = 1010
gossip_para = 2
gossip_para_max = 13
arr_rp = [[1.0,0.8,0.6,0.4,0.2],[1.5,1.2,0.9,0.6,0.3],[2.0,1.6,1.2,0.8,0.4]]
arr_dp = [1.0,1.5,2.0]
while dp < 3
  rp = 0
  while rp < 5
    gossip_select = 1;
    while gossip_select < 4
      gossip_seed = 1000
      while gossip_seed < gossip_seed_max
        graph_seed = 1000
        graph_seed_max = 1050
        while graph_seed < graph_seed_max
          gossip_para = 2
          while gossip_para < gossip_para_max
            file = File.open("Main.java","r")
            buffer = file.read()
            buffer.gsub!(/static final long GOSSIP_SEED =.*/,"static final long GOSSIP_SEED = #{gossip_seed};")
            buffer.gsub!(/static final long GRAPH_SEED =.*/,"static final long GRAPH_SEED = #{graph_seed};")
            buffer.gsub!(/static final int GOSSIP_SELECT =.*/,"static final int GOSSIP_SELECT = #{gossip_select};")
            buffer.gsub!(/static int ffg_k =.*/,"static int ffg_k = #{gossip_para};")
            buffer.gsub!(/static final double D_P =.*/,"static final double D_P = #{arr_dp[dp]};")
            buffer.gsub!(/static final double R_P =.*/,"static final double R_P = #{arr_rp[dp][rp]};")
            
            file.close()
            file = File.open("Main.java","w")
            file.write(buffer)
            file.close()
            `javac Main.java`
            `java Main >> result/res.txt`
            
            flag = 0
            File::open("result/connectivity.txt"){|f|
              if f.gets.to_s == "not connect graph\n" then 
                flag = 1
                break
              end
            }
            if flag == 1 then
              graph_seed_max = graph_seed_max + 1
              break
            end
            if gossip_select == 3 then
              break
            end
            gossip_para = gossip_para + 2
          end
          graph_seed = graph_seed + 1
        end
        gossip_seed = gossip_seed + 1
      end
      gossip_select = gossip_select + 1
    end
    rp = rp + 1
  end
  dp = dp + 1
end


    
        
        
