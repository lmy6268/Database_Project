import requests
import xmltodict
import json
import os 
import time
#정해진 카테고리 분류: 음료 / 아이스크림 / 식품 / 과자 / 생활용품 
snack=("과자")
file=open(f'{os.getcwd()}\m.txt','r')
r=file.readlines()
ID=r[0].strip('\n');PW=r[1].strip('\n')
# cookie=r[2].strip('\n')

# def checkAvailable():
# 	me = {'cookie': cookie}
# 	url = requests.get(
#         'https://developers.naver.com/api/applications/q5_mCT7WPyyPpWp2QN15/usage', headers=me)
# 	quota = json.loads(url.text)['result'][0]['quota']
# 	usage = json.loads(url.text)['result'][0]['usage']
# 	return quota-usage

def search(input,c):
	
    #검색 키워드 입력
	keyword = input

    #조회
	url = f'https://openapi.naver.com/v1/search/shop.json?query={keyword}&display=1'
	header = {'HOST': 'openapi.naver.com',
				'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/94.0.4606.81 Safari/537.36', 'Accept': '* /',
				'X-Naver-Client-Id': f'{ID}',
				'X-Naver-Client-Secret': f'{PW}'}
	#요청 및 응답
	if c>8: #초당 10건밖에 검색 못하므로 1초씩 쉬어가면서 진행
		time.sleep(1)
	res = requests.get(url, headers=header)
	result = res.text

	#전처리 과정
	find = result.find("category1")
	end = result[find:].find('\",')
	find1 = result.find("category2")
	end1 = result[find1:].find('\",')
	find2 = result.find("category3")
	end2 = result[find2:].find('\",')
	#아마 아래 중분류 소분류에서 이중 체크를 거쳐서 최종 카테고리를 분류하는 방식으로 해야 할 것 같다.
	cat = result[find:find+end +1].replace("\"", "").replace("category1: ", "")  # 대분류
	cat1 = result[find1:find1+end1 +1].replace("\"", "").replace("category2: ", "")  # 중분류
	cat2 = result[find2:find2+end2 +1].replace("\"", "").replace("category3: ", "")  # 중분류
	#결과 출력
	if cat=='생활/건강' or cat =='화장품/미용':
		return "생활용품"
	elif cat=="식품":
		if cat1 == "음료":
			return cat1
		if cat1 == "건강식품":
			if cat2 == "건강음료":
				return "음료"
			else:
				return cat
		if cat1 == "아이스크림/빙수":
			return "아이스크림"
		if cat1 == "과자":
			return cat1
		else:
			return cat
	
			
		
	
	
