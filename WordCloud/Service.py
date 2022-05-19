# Copyright(C) 2022 Operacon.
# Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
# https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE

import io
import pkuseg
import wordcloud
import imageio
import re

# 使用 pkuseg 进行分词，由于一天一个群的聊天记录规模不算太大，单进程可以接受
#       此处可以在同目录下的 reserved.txt 定制不会被拆分的词组
seg = pkuseg.pkuseg(user_dict="./reserved.txt")

mask = imread("comment.png")
colors = wordcloud.ImageColorGenerator(mask)

# 配置不在词云中出现的 stopwords
sw = []
with open("stopwords.txt", "r", encoding="UTF-8") as swFile:
    def rl(x):
        return str.strip(x)
    sw = list(map(rl, swFile.readlines()))

# 配置传入词云前多少个最常出现的词（用于改善性能）
maxT = 25

# 使用 wordcloud 进行绘制，注意传入的参数，可自己定制外观
#       此处必须指定系统安装的含有简中的字体，或者放进本目录
#wc = wordcloud.WordCloud(width=1344, height=2772, background_color='white',font_path="simkai.ttf")
wc = wordcloud.WordCloud(width=1921,height=1447,background_color='white', mask=mask, color_func=colors, font_path="simkai.ttf")

def geneImg(t):
    #去除url链接
    urlRe = re.compile(r"[a-zA-z]+://[^\s]*")
    t = re.sub(urlRe, "", t)
    
    lis = seg.cut(t)
    maxN = min(maxT, lis.__len__())
    dic = {}
    res = {}
    j = 0
    for i in lis:
        if(dic.get(i) == None):
            dic[i] = 1
        else:
            dic[i] = dic[i] + 1
    dic = dict(sorted(dic.items(), key=lambda x: x[1], reverse=True))
    for i in dic.keys():
        if(i in sw):
            continue
        res[i] = dic[i] / lis.__len__() * maxN
        j = j + 1
        if j == maxN:
            break
    b = io.BytesIO()
    wc.generate_from_frequencies(res)
    wc.to_image().save(b, format="png")
    return b.getvalue()
