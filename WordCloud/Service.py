# Copyright(C) 2022 Operacon.
# Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
# https://github.com/Operacon/XiaoXiang-miraiBot/blob/main/LICENSE

import io
import pkuseg
import wordcloud
import skimage
import re

# 使用 pkuseg 进行分词，由于一天一个群的聊天记录规模不算太大，单进程可以接受
#       此处可以在同目录下的 reserved.txt 定制不会被拆分的词组
seg = pkuseg.pkuseg(user_dict="./reserved.txt")

# 生成词云的蒙版图片，前景和背景对比应当比较强烈
#       替换同目录下的 mask.png 来定制蒙版
enable_mask = True
mask = skimage.io.imread("./mask.png")
mask = skimage.img_as_ubyte(
    skimage.transform.resize(
        mask, (1344 / mask.shape[1] * mask.shape[0], 1344)))
colors = wordcloud.ImageColorGenerator(mask)

# 配置不在词云中出现的 stopwords
sw = []
with open("stopwords.txt", "r", encoding="UTF-8") as swFile:
    def rl(x):
        return str.strip(x)
    sw = list(map(rl, swFile.readlines()))

# 配置传入词云前多少个最常出现的词（用于改善性能）
maxT = 25

# wordcloud 进行绘制使用的字体和背景颜色
#       此处必须指定系统安装的含有简中的字体，或者放进本目录
#       颜色格式为标准色名或 rgb 函数，前景色默认为蒙版前景色，如不启用蒙版则随机生成
font_path = "simkai.ttf"
background_color = "rgb(250, 235, 215)"  # "black"

# 可重写 color_func 来修改前景色
if enable_mask:
    wc = wordcloud.WordCloud(background_color=background_color,
                             mask=mask, color_func=colors,
                             font_path=font_path)
else:
    wc = wordcloud.WordCloud(width=1344, height=2772,
                             background_color=background_color,
                             font_path=font_path)
urlRe = re.compile(r"[a-zA-z]+://[^\s]*")


def geneImg(t):
    # 去除url链接
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
