import pandas as pd
import os 
a= pd.read_excel(f'{os.getcwd()}/DB.xlsx',nrows=2, usecols = 'A:D')
print(a)