import os
import pandas as pd
import googlemaps
import time
import requests, json
import numpy as np
from path import *

from selenium import webdriver
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
from selenium.webdriver.support import expected_conditions as EC  
from selenium.common.exceptions import *

options = webdriver.ChromeOptions()
options.add_argument('--no-sandbox')
options.add_argument('--disable-dev-shm-usage')

gmaps = googlemaps.Client(key=api_key)

def isNaN(num):
    return num == ""

def get_inner_html(element):
    return element.get_attribute('innerHTML')

def get_outer_html(element):
    return element.get_attribute('outerHTML')

def wait_element(driver, selector, second = 10):
    return WebDriverWait(driver,second).until(EC.presence_of_element_located((By.CSS_SELECTOR, selector)))

if __name__ == "__main__":
    df = pd.DataFrame({'shop':[], 'phone':[], 'location':[]})

    driver = webdriver.Chrome(chrome_path, options=options)
    driver.implicitly_wait(3)
    driver.get(web_path)

    location_paging_script= [            
            "$.setMainButton('서울특별시');$.searchGUGUN('서울특별시', '','2'); $.searchData('서울', ''); $(this).focus(); return false;",
            "$.setMainButton('경기도');$.searchGUGUN('경기도', '','2'); $.searchData('경기', ''); $(this).focus(); return false;",
            "$.setMainButton('부산광역시');$.searchGUGUN('부산광역시', '','2'); $.searchData('부산', ''); $(this).focus(); return false;",
            "$.setMainButton('대구광역시');$.searchGUGUN('대구광역시', '','2'); $.searchData('대구', ''); $(this).focus(); return false;",
            "$.setMainButton('인천광역시');$.searchGUGUN('인천광역시', '','2'); $.searchData('인천', ''); $(this).focus(); return false;",
            "$.setMainButton('대전광역시');$.searchGUGUN('대전광역시', '','2'); $.searchData('대전', ''); $(this).focus(); return false;",
            "$.setMainButton('울산광역시');$.searchGUGUN('울산광역시', '','2'); $.searchData('울산', ''); $(this).focus(); return false;",
            "$.setMainButton('강원도');$.searchGUGUN('강원도', '','2'); $.searchData('강원', ''); $(this).focus(); return false;",
            "$.setMainButton('충청북도');$.searchGUGUN('충청북도', '','2'); $.searchData('충북', ''); $(this).focus(); return false;",
            "$.setMainButton('충청남도');$.searchGUGUN('충청남도', '','2'); $.searchData('충남', ''); $(this).focus(); return false;",
            "$.setMainButton('광주광역시');$.searchGUGUN('광주광역시', '','2'); $.searchData('광주', ''); $(this).focus(); return false;",
            "$.setMainButton('전라북도');$.searchGUGUN('전라북도', '','2'); $.searchData('전북', ''); $(this).focus(); return false;",
            "$.setMainButton('전라남도');$.searchGUGUN('전라남도', '','2'); $.searchData('전남', ''); $(this).focus(); return false;",
            "$.setMainButton('경상북도');$.searchGUGUN('경상북도', '','2'); $.searchData('경북', ''); $(this).focus(); return false;",
            "$.setMainButton('경상남도');$.searchGUGUN('경상남도', '','2'); $.searchData('경남', ''); $(this).focus(); return false;",
            "$.setMainButton('제주특별자치도');$.searchGUGUN('제주특별자치도', '','2'); $.searchData('제주', ''); $(this).focus(); return false;",
            "$.setMainButton('세종특별자치시');$.searchGUGUN('세종특별자치시', '','2'); $.searchData('세종', ''); $(this).focus(); return false;"]

    location_paging_limit = [
        87, #서울
        103, #경기
        26, #부산
        22, #대구
        24, #인천
        13, #대전
        10, #울산
        13, #강원
        14, #충북
        20, #충남
        14, #광주
        16, #전북
        15, #전남
        20, #경북
        28, #경남
        5, #제주
        2, #세종
    ]

    df_index = 0
    try:
        while True:
            for i, script in enumerate(location_paging_script):
                #지역 변경
                driver.execute_script(script)
                time.sleep(3)

                #페이지 변경
                for j in range(1,location_paging_limit[i]+1):
                    driver.execute_script(f"$.selfSubmit('{j}')")
                    time.sleep(3)

                    print(f'{j}')
                    table = driver.find_element_by_css_selector('.tbl_data')
                    tr_list = table.find_elements_by_css_selector('tbody > tr')
                    for tr in tr_list:
                        df.loc[df_index,'shop'] = tr.find_element_by_css_selector('td:nth-child(1)').text
                        df.loc[df_index,'phone'] = tr.find_element_by_css_selector('td:nth-child(2)').text
                        df.loc[df_index,'location'] = tr.find_element_by_css_selector('td:nth-child(3)').text
                        df_index+=1

                    #페이지 끝날 시 저장
                    print('save csv')
                    df.to_csv(csv_path, encoding='utf-8')
    except:
        #비정상 종료 시 저장
        print('save csv')
        df.to_csv(csv_path, encoding='utf-8')
    
    df.to_csv(csv_path, encoding='utf-8')
    driver.quit()
