import json
import numpy as np
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression, RandomizedLogisticRegression
from sklearn.multiclass import OneVsRestClassifier
from sklearn.preprocessing import Normalizer
from sklearn.pipeline import Pipeline
from sklearn.feature_selection import SelectKBest, chi2

MAX_LABEL = 250

def get_useless(labels):
    useless = []
    for i in range(MAX_LABEL):
        has = list(filter(lambda x: x[i] == 1, labels))
        if len(has) == 0:
            useless.append(i)
    return useless


def convert(us, labels):
    ret = []
    for label in labels:
        cur = []
        for i in range(MAX_LABEL):
            if i not in us:
                cur.append(label[i])
        ret.append(cur)

    return ret

t, ev = map(int, raw_input().split())

labels = []
texts = []
for i in range(t):
    lb = [0 for i in range(MAX_LABEL)]
    for x in map(int, raw_input().split()[1:]):
        lb[x] = 1

    labels.append(np.array(lb))

    texts.append(raw_input())

useless = get_useless(labels)
rconv = dict()
for i in range(MAX_LABEL):
    if i not in useless:
        rconv[len(rconv)] = i
labels = convert(useless, labels)

labels = np.array(labels)
vectorizer = TfidfVectorizer(ngram_range=(1,1), max_df=0.8)
texts = vectorizer.fit_transform(texts)

logr = LogisticRegression(C=4, class_weight='auto')
selector = SelectKBest(chi2, k=1000)

myLog = Pipeline([('sel', selector), ('logr', logr)])

model = OneVsRestClassifier(myLog)
model.fit(texts, labels)

exp = []
for i in range(ev):
    text = raw_input()
    exp.append(text)

exp = vectorizer.transform(exp)

answer = model.predict_proba(exp)
for row in answer:
    classes = sorted(list(enumerate(row)), key = lambda y: -y[1])
    print ' '.join(map(lambda x: str(rconv[x]), [rr[0] for rr in classes[:1]]))
    