#include <iostream>
#include <vector>
#include <map>
#include <fstream>
#include <cmath>
using namespace std;
void write_file(string,string);
double calc_standard_deviation(vector<double>,double);
int main(){
  map<pair<pair<double,double>,pair<double,double> > ,double> rea;
  map<pair<pair<double,double>,pair<double,double> > ,double> rel;
  map<pair<pair<double,double>,pair<double,double> > ,double> mes;
  map<pair<pair<double,double>,pair<double,double> > ,double> hop;
  map<pair<pair<double,double>,pair<double,double> > ,double> cnt;


  map<pair<pair<double,double>,pair<double,double> > ,vector<double> > mes_by_hop;
  map<pair<pair<double,double>,pair<double,double> > ,vector<double> > rea_by_hop;
  map<pair<pair<double,double>,pair<double,double> > ,vector<double> > hop_cnt;


  map<pair<pair<double,double>,pair<double,double> > ,vector<pair<int,double> > > mes_by_hop_tes;
  map<pair<pair<double,double>,pair<double,double> > ,vector<pair<int,double> > > rea_by_hop_tes;

  //分散
  map<pair<pair<double,double>,pair<double,double> > ,vector<double> > keep_rea;
  map<pair<pair<double,double>,pair<double,double> > ,vector<double> > keep_mes;
  map<pair<pair<double,double>,pair<double,double> > ,vector<double> > keep_hop;

  int cnt_t = 0;
  int n;
  while(cin >> n){
	vector<double> tmp_mes_by_hop(n);
	vector<double> tmp_rea_by_hop(n);
	for(int i = 0;i < n;i++){
	  cin >> tmp_rea_by_hop[i] >> tmp_mes_by_hop[i];
	}
   
	int gossip_select_t,gossip_p_t,gossip_seed_t,graph_seed_t;
	double dp_t,rp_t;
	int flooding_mes_t,mes_t;
	double rea_t;
	int hop_t;
	cin >> gossip_select_t >> gossip_p_t >> gossip_seed_t >> graph_seed_t >> dp_t >> rp_t >> flooding_mes_t >> mes_t >> rea_t >> hop_t;
	
	pair<double,double> dprp(dp_t,rp_t);
	pair<int,double> gossip_sp(gossip_select_t,gossip_p_t);
	pair<pair<double,double>,pair<int,double> > key(dprp,gossip_sp);
	
	rea[key] += rea_t;
	if(rea_t == 1){
	  rel[key]+=1;
	}
	mes[key] += mes_t;
	hop[key] += hop_t;
	cnt[key]++;
	if(mes_by_hop.count(key) == 0){
	  mes_by_hop[key] = vector<double>(350);
	  rea_by_hop[key] = vector<double>(350);
	  hop_cnt[key] = vector<double>(350);

	  //
	  mes_by_hop_tes[key] = vector<pair<int,double> >();
	  rea_by_hop_tes[key] = vector<pair<int,double> >();
	  //
	}

	for(int i = 0;i < 350;i++){
	  //
	  mes_by_hop_tes[key].push_back(pair<int,double>(i,tmp_mes_by_hop[i]));
	  rea_by_hop_tes[key].push_back(pair<int,double>(i,tmp_rea_by_hop[i]));
	  //
	  if(tmp_mes_by_hop.size() <= i){
		int b_i = tmp_mes_by_hop.size() - 1;
		mes_by_hop[key][i] += tmp_mes_by_hop[b_i];
		rea_by_hop[key][i] += tmp_rea_by_hop[b_i];
		hop_cnt[key][i]++;
	  }else{
		mes_by_hop[key][i] += tmp_mes_by_hop[i];
		rea_by_hop[key][i] += tmp_rea_by_hop[i];
		hop_cnt[key][i]++;
	  }
	  // if(mes_by_hop[key].size() <= i){
	  // 	mes_by_hop[key].push_back(tmp_mes_by_hop[i]);
	  // 	rea_by_hop[key].push_back(tmp_rea_by_hop[i]);
	  // 	hop_cnt[key].push_back(1);
	  // 	if(i != 0){
	  // 	  mes_by_hop[key][i] += mes_by_hop[key][i-1];
	  // 	  rea_by_hop[key][i] += rea_by_hop[key][i-1];
	  // 	  hop_cnt[key][i] += hop_cnt[key][i-1];
	  // 	}
	  // }else{
	  // 	mes_by_hop[key][i] += tmp_mes_by_hop[i];
	  // 	rea_by_hop[key][i] += tmp_rea_by_hop[i];
	  // 	hop_cnt[key][i]++;
	  // }
	  //
	}
	if(keep_rea.count(key) == 0){
	  keep_rea[key] = vector<double>();
	  keep_mes[key] = vector<double>();
	  keep_hop[key] = vector<double>();
	}
	keep_rea[key].push_back(rea_t);
	keep_mes[key].push_back(mes_t);
	keep_hop[key].push_back(hop_t);
  }

  //ファイルをつくる
  vector<double> dp(3);
  vector<vector<double> > rp(3,vector<double>(5,5));
  dp[0] = 1.0;dp[1] = 1.5;dp[2] = 2.0;
  rp[0][0] = 1.0;
  rp[0][1] = 0.8;
  rp[0][2] = 0.6;
  rp[0][3] = 0.4;
  rp[0][4] = 0.2;
  rp[1][0] = 1.5;
  rp[1][1] = 1.2;
  rp[1][2] = 0.9;
  rp[1][3] = 0.6;
  rp[1][4] = 0.3;
  rp[2][0] = 2.0;
  rp[2][1] = 1.6;
  rp[2][2] = 1.2;
  rp[2][3] = 0.8;
  rp[2][4] = 0.4;
  vector<double> k(5);
  k[0] = 1.0;k[1] = 0.8;k[2] = 0.6;k[3] = 0.4;k[4] = 0.2;
  for(int i = 0;i < 3;i++){//dp
	for(int l = 1;l < 4;l++){//gossip_s
	  for(int m = 2;m < 13;m+=2){//gossip_para
		pair<int,double> gossip_sp(l,m);
		string tmp = "gnufile/" + to_string(dp[i]) + "_";

		if(l == 1){
		  tmp+="biconnected_";
		}else if(l == 2){
		  tmp+="fixed_fanout_";
		}else if(l == 3){
		  tmp+="decision_";
		}
		if(l != 3){
		  tmp+=to_string(m) + "_";
		}
		string filename_rea = tmp + "rea";
		string filename_rel = tmp + "rel";
		string filename_mes = tmp + "mes";
		string filename_hop = tmp + "hop";
		
		string rea_cont = "";
		string rel_cont = "";
		string mes_cont = "";
		string hop_cont = "";
		for(int j = 0;j < 5;j++){// rp
		  pair<double,double> dprp(dp[i],rp[i][j]);
		  pair<pair<double,double>,pair<int,double> > key(dprp,gossip_sp);
		  //cout << "dp = "  << dp[i] << "rp = " << rp[i][j] << " " << l << " " << m << endl;
		  //cout << rea[key] << " " << cnt[key] << endl;
		  double ave_rea = rea[key]/cnt[key];
		  rea_cont += to_string(k[j]) + " " + to_string(ave_rea) + " " + to_string(calc_standard_deviation(keep_rea[key],ave_rea)) + "\n";
		  double ave_rel = rel[key]/cnt[key];
		  rel_cont += to_string(k[j]) + " " + to_string(ave_rel) + "\n";
		  double ave_mes = mes[key]/cnt[key];
		  mes_cont += to_string(k[j]) + " " + to_string(ave_mes) + " " + to_string(calc_standard_deviation(keep_mes[key],ave_mes)) + "\n";
		  double ave_hop = hop[key]/cnt[key];
		  hop_cont += to_string(k[j]) + " " + to_string(ave_hop) + " " + to_string(calc_standard_deviation(keep_hop[key],ave_hop)) + "\n";
		  
		}

		write_file(filename_rea,rea_cont);
		write_file(filename_rel,rel_cont);
		write_file(filename_mes,mes_cont);
		write_file(filename_hop,hop_cont);
		if(l == 3)break;
	  }
	}
  }
  for(int i = 0;i < 3;i++){
	for(int j = 0;j < 5;j++){
	  for(int l = 1;l < 4;l++){
		for(int m = 2;m < 13;m+=2){
		  pair<double,double> dprp(dp[i],rp[i][j]);
		  pair<double,int> gossip_sp(l,m);
		  pair<pair<double,double> ,pair<double,int> > key(dprp,gossip_sp);
		  string tmp = "gnufile/" + to_string(dp[i]) + "_" + to_string(rp[i][j]) + "_";
		  if(l == 1){
			tmp+= "biconnected_";
		  }else if(l == 2){
			tmp+= "fixed_fanout_";
		  }else if(l == 3){
			tmp+= "decision_";
		  }
		  if(l != 3){
			tmp += to_string(m) + "_";
		  }
		  string filename_rea_by_hop = tmp + "rea_by_hop";
		  string filename_mes_by_hop = tmp + "mes_by_hop";
		  string rea_by_hop_cont = "";
		  string mes_by_hop_cont = "";
		  for(int o = 0;o < rea_by_hop[key].size();o++){
			rea_by_hop_cont += to_string(o) + " " + to_string(rea_by_hop[key][o]/hop_cnt[key][o]) + "\n";
			mes_by_hop_cont += to_string(o) + " " + to_string(mes_by_hop[key][o]/hop_cnt[key][o]) + "\n";
		  }
		  write_file(filename_rea_by_hop,rea_by_hop_cont);
		  write_file(filename_mes_by_hop,mes_by_hop_cont);
		  if(l == 3)break;
		}

	  }
	}
  }
  for(int i = 0;i < 3;i++){
	for(int j = 0;j < 5;j++){
	  for(int l = 1;l < 4;l++){
		for(int m = 2;m < 13;m+=2){
		  pair<double,double> dprp(dp[i],rp[i][j]);
		  pair<double,int> gossip_sp(l,m);
		  pair<pair<double,double> ,pair<double,int> > key(dprp,gossip_sp);
		  string tmp = "gnufile/" + to_string(dp[i]) + "_" + to_string(rp[i][j]) + "_";
		  if(l == 1){
			tmp+= "biconnected_";
		  }else if(l == 2){
			tmp+= "fixed_fanout_";
		  }else if(l == 3){
			tmp+= "decision_";
		  }
		  if(l != 3){
			tmp += to_string(m) + "_";
		  }
		  string filename_rea_by_hop = tmp + "rea_by_hop_tes";
		  string filename_mes_by_hop = tmp + "mes_by_hop_tes";
		  string rea_by_hop_cont = "";
		  string mes_by_hop_cont = "";
		  for(int o = 0;o < rea_by_hop_tes[key].size();o++){
			rea_by_hop_cont += to_string(rea_by_hop_tes[key][o].first) + " " + to_string(rea_by_hop_tes[key][o].second) + "\n";
			mes_by_hop_cont += to_string(mes_by_hop_tes[key][o].first) + " " + to_string(mes_by_hop_tes[key][o].second) + "\n";
		  }
		  write_file(filename_rea_by_hop,rea_by_hop_cont);
		  write_file(filename_mes_by_hop,mes_by_hop_cont);
		  if(l == 3)break;
		}
	  }
	}
  }

												
  
  
  
// //  pair<double,double>
// //  write_file("aiueo","dfaf");
//   map<pair<double,double>, double> reachability;
//   map<pair<double,double>, double> flooding_mes;
//   map<pair<double,double>, double> biconnected_mes;
//   map<pair<double,double>, double> hop;
//   map<pair<double,double>, int> cnt;
//   map<pair<double,double>, vector<double> > rea_by_hop;
//   map<pair<double,double>, vector<int> > mes_by_hop;
//   map<pair<double,double>, vector<int> > rea_by_hop_cnt;


//   vector<double> dp(3);
//   vector<vector<double> > rp(3,vector<double>(5,5));
//   dp[0] = 1.0;
//   dp[1] = 1.5;
//   dp[2] = 2.0;
//   rp[0][0] = 1.0;
//   rp[0][1] = 0.8;
//   rp[0][2] = 0.6;
//   rp[0][3] = 0.4;
//   rp[0][4] = 0.2;
//   rp[1][0] = 1.5;
//   rp[1][1] = 1.2;
//   rp[1][2] = 0.9;
//   rp[1][3] = 0.6;
//   rp[1][4] = 0.3;
//   rp[2][0] = 2.0;
//   rp[2][1] = 1.6;
//   rp[2][2] = 1.2;
//   rp[2][3] = 0.8;
//   rp[2][4] = 0.4;
//   map<pair<double,double>, vector<double> > keep_rea;
//   map<pair<double,double>, vector<double> > keep_fl_mes;
//   map<pair<double,double>, vector<double> > keep_bi_mes;
//   map<pair<double,double>, vector<double> > keep_hop;

//   int gossip_seed;
//   int graph_seed;
//   int n = 0;
//   while(cin >> n){
// 	vector<double> keep_rea_by_hop_tmp(n);
// 	vector<double> keep_mes_by_hop_tmp(n);
// 	vector<double> keep_rea_by_hop_cnt_tmp(n);
// 	for(int i = 0;i < n;i++){
// 	  cin >> keep_rea_by_hop_tmp[i] >> keep_mes_by_hop_tmp[i];
// 	}
// 	cin >> graph_seed;
// 	cin >> gossip_seed;
// 	double dp_d,rp_d;
// 	int fl_mes;
// 	int bi_mes;
// 	double rea;
// 	int h;
// 	cin >> dp_d >> rp_d >> fl_mes >> bi_mes >> rea >> h;
// 	pair<double,double> dprp(dp_d,rp_d);
// 	if(keep_rea.count(dprp) == 0){
// 	  keep_rea[dprp] = vector<double>();
// 	  keep_fl_mes[dprp] = vector<double>();
// 	  keep_bi_mes[dprp] = vector<double>();
// 	  keep_hop[dprp] = vector<double>();
// 	  rea_by_hop[dprp] = vector<double>();
// 	  mes_by_hop[dprp] = vector<int>();
// 	  rea_by_hop_cnt[dprp] = vector<int>();
// 	}
// 	keep_rea[dprp].push_back(rea);
// 	keep_fl_mes[dprp].push_back(fl_mes);
// 	keep_bi_mes[dprp].push_back(bi_mes);
// 	keep_hop[dprp].push_back(h);

// 	reachability[dprp] += rea;
// 	flooding_mes[dprp] += fl_mes;
// 	biconnected_mes[dprp] += bi_mes;
// 	hop[dprp] += h;
// 	cnt[dprp] += 1;
// 	for(int i = 0;i < n;i++){
// 	  if(rea_by_hop[dprp].size() <= i){
// 		rea_by_hop[dprp].push_back(keep_rea_by_hop_tmp[i]);
// 		mes_by_hop[dprp].push_back(keep_mes_by_hop_tmp[i]);
// 		rea_by_hop_cnt[dprp].push_back(1);
// 	  }else{
// 		rea_by_hop[dprp][i]+=keep_rea_by_hop_tmp[i];
// 		mes_by_hop[dprp][i]+=keep_mes_by_hop_tmp[i];
// 		rea_by_hop_cnt[dprp][i]++;
// 	  }
// 	}

//   }



//   for(int i = 0;i < dp.size();i++){
// 	string filename_rea = "gnufile/" + to_string(dp[i]) + "_" + "rea";
// 	string filename_bi_mes = "gnufile/" + to_string(dp[i]) + "_" + "bi_mes";
// 	string filename_fl_mes = "gnufile/" + to_string(dp[i]) + "_" + "fl_mes";
// 	string filename_hop = "gnufile/" + to_string(dp[i]) + "_" + "hop";

// 	string content_rea = "";
// 	string content_bi_mes = "";
// 	string content_fl_mes = "";
// 	string content_hop = "";
	
// 	for(int j = 0;j < rp[i].size();j++){
// 	  pair<double,double> key_pair(dp[i],rp[i][j]);
// 	  int value_cnt = cnt[key_pair];
// 	  double value_rea = reachability[key_pair]/value_cnt;
// 	  double value_fl_mes = flooding_mes[key_pair]/value_cnt;
// 	  double value_bi_mes = biconnected_mes[key_pair]/value_cnt;
// 	  double value_hop = hop[key_pair]/value_cnt;
// 	  double k = rp[i][j]/dp[i];
// 	  content_rea += to_string(k) + " ";
// 	  content_rea += to_string(value_rea) + " ";
// 	  content_rea += to_string(calc_standard_deviation(keep_rea[key_pair],value_rea)) + "\n";
	  
// 	  content_fl_mes += to_string(k) + " ";
// 	  content_fl_mes += to_string(value_fl_mes) + " ";
// 	  content_fl_mes += to_string(calc_standard_deviation(keep_fl_mes[key_pair],value_fl_mes)) + "\n";
	  
// 	  content_bi_mes += to_string(k) + " ";
// 	  content_bi_mes += to_string(value_bi_mes) + " ";
// 	  content_bi_mes += to_string(calc_standard_deviation(keep_bi_mes[key_pair],value_bi_mes)) + "\n";

// 	  content_hop += to_string(k) + " ";
// 	  content_hop += to_string(value_hop) + " ";
// 	  content_hop += to_string(calc_standard_deviation(keep_hop[key_pair],value_hop)) + "\n";
// 	  {
// 		string filename_rea_by_hop = "gnufile/" + to_string(dp[i]) + "_" + to_string(rp[i][j]) + "_rea_by_" + "hop";
// 		string filename_mes_by_hop = "gnufile/" + to_string(dp[i]) + "_" + to_string(rp[i][j]) + "_mes_by_" + "hop";
// 		string content_rea_by_hop = "";
// 		string content_mes_by_hop = "";
// 		for(int l = 0;l < rea_by_hop[key_pair].size();l++){
// 		  content_rea_by_hop += to_string(l) + " " + to_string(rea_by_hop[key_pair][l]/rea_by_hop_cnt[key_pair][l])  + "\n"; 
// 		  content_mes_by_hop += to_string(l) + " " + to_string(mes_by_hop[key_pair][l]/rea_by_hop_cnt[key_pair][l]) + "\n";
// 		}
// 		write_file(filename_rea_by_hop,content_rea_by_hop);
// 		write_file(filename_mes_by_hop,content_mes_by_hop);
// 	  }
// 	}
// 	write_file(filename_rea,content_rea);
// 	write_file(filename_fl_mes,content_fl_mes);
// 	write_file(filename_bi_mes,content_bi_mes);
// 	write_file(filename_hop,content_hop);
//   }
  
//   return 0;
}
double calc_standard_deviation(vector<double> vec,double mid){
  int size = vec.size();
  double v = 0;
  for(int i = 0;i < vec.size();i++){
	double val = vec[i];
	v += (val - mid)*(val - mid);
  }
  return sqrt(v/size);
}
void write_file(string filename,string content){
  ofstream ofs;
  ofs.open(filename,ios::ate);
  ofs << content;
  ofs.close();
}
