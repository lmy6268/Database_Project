import difflib
from functools import reduce
import re
chut = 'ㄱㄲㄴㄷㄸㄹㅁㅂㅃㅅㅆㅇㅈㅉㅊㅋㅌㅍㅎ#'
ga = 'ㅏㅐㅑㅒㅓㅔㅕㅖㅗㅘㅙㅚㅛㅜㅝㅞㅟㅠㅡㅢㅣ#'
ggut = ' ㄱㄲㄳㄴㄵㄶㄷㄹㄺㄻㄼㄽㄾㄿㅀㅁㅂㅄㅅㅆㅇㅈㅊㅋㅌㅍㅎ#'

BASE = 0xAC00

query = '췟'

code = ord(query) - BASE

jongsung = code % 28
jungsung = ((code-jongsung) // 28) % 21
chosung = ((code - jongsung) // 28) // 21

def segment(ch):
    '''유니코드 글자를 입력받아 초,중,종성에 대한 인덱스를 반환한다'''
    code = ord(ch) - BASE
    jongsung = code % 28
    
    code = code - jongsung
    jungsung = (code // 28) % 21
    
    code = code // 28
    chosung = code // 21
    
    if chosung < 0:
        chosung = -1
    if 19 < jongsung:
        jongsung = -1
    
    return chut[chosung], ga[jungsung], ggut[jongsung]


def diff(word1, word2):
    '''두 유니코드 단어의 거리를 계산하여 차이를 반환한다'''
    L1 = ''.join(reduce(lambda x1,x2: x1+x2, map(segment, word1)))
    L2 = ''.join(reduce(lambda x1,x2: x1+x2, map(segment, word2)))
    differ = difflib.SequenceMatcher(None, L1, L2)
    return differ.ratio()


def isKorean(text):
    hangul = re.compile('[\u3131-\u3163\uac00-\ud7a3]+')  
    result = hangul.findall(text)
    return len(result)
