実行の仕方

まずexec.rbを実行する。
実行後、result/res.txtに結果が保存される。
result/average_reachability.ccをコンパイル(gcc -O2 average_reachability.cc)
その後result/res.txtを入力として実行ファイル(a.out)を実行(./a.out < res.txt)
result/gnufile内に、gnuplotの入力形式で、ファイルが生成される。

DP:実数 Dense areaのノード数のパラメータ
RP:実数 Sparse areaのノード数のパラメータ
ゴシップアルゴリズム:fixed_fanout => FFG , biconnected => GAMBC, decision => MDAMBC
パラメータ:整数 ゴシップアルゴリズムのパラメータ(MDAMBCにはパラメータなし)
種類:rea=>reachability, mes=>メッセージ数,hop=>ホップ数,　rel=>reliability

”DP_RP_ゴシップアルゴリズム_パラメータ_種類_by_hop”はホップ数ごとの種類(rea,mes)の値を示している(縦軸 種類,横軸 ホップ数)

"DP_ゴシップアルゴリズム_パラメータ_種類"はDPの時の種類(rea,nes,hop,rel)の値を示している(縦軸 種類,横軸 k[skewness factor])

これをグラフ化するには、result/gnufileのディレクトリー内の
gnu_hop.py 
gnu_mes.py
gnu_rea.py
gnu_rel.py
gnu_rea_by_hop.py
gnu_mes_by_hop.py
を実行する(python ファイル名)
実行するとresult/gnugile内にpng形式で、グラフができます。


修士論文で使用したファイルはresult/gnufile/png内に格納しています。

"DP_RP_種類_by_hop.png"はホップ数ごとの種類(rea,mes)のグラフです。(縦軸 種類、横軸 ホップ数)

"DP_種類.png"はDPの時の種類(rea,mes,rel,hop)のグラフです。(縦軸 種類, 横軸 k[skewness factor])






