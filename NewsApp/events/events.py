import json
path="./events.txt"
import numpy as np


from sklearn.feature_extraction.text import TfidfVectorizer, CountVectorizer, TfidfTransformer
from sklearn.cluster import KMeans
#from sklearn.externals import joblib

text_list=[]
id_list=[]
word_list=[]

with open(path, "r",encoding="utf-8") as ff:
    label=ff.read()
   # print(label)
    json_arr=json.loads(label)
    for obj in json_arr:
        id=obj['_id']
        seg_text=obj['seg_text']

        id_list.append(id)
        text_list.append(seg_text)
       # print(text_list)


print(len(id_list))

tfidf_vectorizer = TfidfVectorizer(lowercase=False)
'''
tokenizer: 指定分词函数
lowercase: 在分词之前将所有的文本转换成小写，因为涉及到中文文本处理，
所以最好是False
'''



# 需要进行聚类的文本集
vectorizer = CountVectorizer()
transformer = TfidfTransformer()
tfidf=transformer.fit_transform(vectorizer.fit_transform(text_list))


word = vectorizer.get_feature_names()
print(word)

tfidf_weight = tfidf.toarray()
kmeans = KMeans(n_clusters=14,random_state=0)
kmeans.fit(tfidf_weight)

karr=[]
with open("keyword.txt","r",encoding="utf-8") as f:
    list=f.readlines()
    for i in list:
        karr.append(int(i))


with open("keyword_ch.txt","w",encoding="utf-8") as f:
    for (j,i) in enumerate(kmeans.cluster_centers_):
        arr=np.array(i)
        top_k=3
        top_k_idx=arr.argsort()[::-1][0:top_k]
        print(word[top_k_idx[karr[j]]])
        f.write(word[top_k_idx[karr[j]]]+" ")


#km_cluster = KMeans(n_clusters=num_clusters, max_iter=300, n_init=40,
 #                   init='k-means++', n_jobs=-1)
'''
n_clusters: 指定K的值
max_iter: 对于单次初始值计算的最大迭代次数
n_init: 重新选择初始值的次数
init: 制定初始值选择的算法
n_jobs: 进程个数，为-1的时候是指默认跑满CPU
注意，这个对于单个初始值的计算始终只会使用单进程计算，
并行计算只是针对与不同初始值的计算。比如n_init=10，n_jobs=40, 
服务器上面有20个CPU可以开40个进程，最终只会开10个进程
'''

# 返回各自文本的所被分配到的类索引

st=""
result = kmeans.fit_predict(tfidf_weight)
for x in result:
    st=st+str(x)+" "

st=st[0:-2]
print(st[-1])

with open("type.json","w",encoding="utf-8") as f:
    f.write(st)

print("Predicting result:", result)
'''
每一次fit都是对数据进行拟合操作，
所以我们可以直接选择将拟合结果持久化，
然后预测的时候直接加载，进而节省时间。
'''

#joblib.dump(tfidf_vectorizer, 'tfidf_fit_result.pkl')
#joblib.dump(km_cluster, 'km_cluster_fit_result.pkl')

# 程序下一次则可以直接load
#tfidf_vectorizer = joblib.load('tfidf_fit_result.pkl')
#km_cluster = joblib.load('km_cluster_fit_result.pkl')




