#!/usr/bin/python                                                                                                                                     
#coding:utf-8                                                                                                                                                                      
import os
dp_list=[1,1.5,2]
k_list=[0.2,0.4,0.6,0.8,1.0]
para_list=[2,4,6,8,12]

pt_list = [80,90,48,52,1,2,4,6,8,10,12,14];
color_list=["10","40","70","A0","D0","FF"]
color=["ff0000","ffff00","00ff00","006400","00ffff","ff00ff","ffc0cb","fa8072","f0e68c","ffa500","a020f0","bebebe"]
TMPL1=3*[0]
TMPL2=4*[0]
TMPL3=3*[0]
TMPL1[0]='echo \"set xlabel \\" k \\" \\n set ylabel \\" Num of message \\" \\n set xrange[0:1] \\n set yrange [0:6000] \\n  set terminal png size 1024,1024\\n set out '
TMPL1[1]=' \\"%1.1f_mes.png\\"'
TMPL1[2]=' \\n set style line 1 lt 3  \\n plot '

TMPL2[0]=' \\"%1.6f_biconnected_%d_mes\\"'
TMPL2[1]=' title \\"GAMBC message fanout = %d\\" with errorlines  pt %d ps %d lw 2 lc rgb \\"#%s\\",'

TMPL2[2]=' \\"%1.6f_fixed_fanout_%d_mes\\"'
TMPL2[3]=' title \\"FFG message fanout = %d\\" with errorlines  pt %d ps %d lw 2 lc rgb \\"#%s\\",'

TMPL3[0]=' \\"%1.6f_decision_mes\\"'
TMPL3[1]=' title \\"MBCMDA message\\" with errorlines  pt 4 ps 5 lw 2 lc rgb \\"#0000B0\\" \"'
TMPL3[2]=' | gnuplot'



for dp in dp_list:
    string=""
    c_cnt=0
    pt_cnt=0
    ps_size = 1    
    for tmp in TMPL1:
        string+=tmp
    string=string%(dp)
    for i in range(0,5):
        for tmp2 in TMPL2:
            string+=tmp2
        string=string%(dp,para_list[i],para_list[i],pt_list[pt_cnt],ps_size,color[c_cnt],dp,para_list[i],para_list[i],pt_list[pt_cnt+1],ps_size,color[c_cnt+1])
        c_cnt+=2
        pt_cnt+=2
        ps_size+=1
    for tmp3 in TMPL3:
        string+=tmp3
    string=string%(dp)
    print(string)
    os.system(string)


       











