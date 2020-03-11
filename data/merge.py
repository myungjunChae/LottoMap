import pandas as pd

main=pd.read_csv('./data.csv')
first=pd.read_csv('./first.csv')
second=pd.read_csv('./second.csv')

t1=pd.merge(main,first,on="location",how="outer")
t2=pd.merge(t1,second,on="location",how="outer")
t2.to_csv('./final.csv')
