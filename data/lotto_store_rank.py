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
    df = pd.DataFrame({'shop':[], '1st':[], 'location':[]})

    driver = webdriver.Chrome(chrome_path, options=options)
    driver.implicitly_wait(3)
    driver.get("https://dhlottery.co.kr/store.do?method=topStoreRank&rank=2&pageGubun=L645")

    df_index = 0
    csv = os.getcwd() + '/2st.csv'
    try:
        #페이지 변경
        for j in range(1,449):
            driver.execute_script(f"selfSubmit('{j}')")
            time.sleep(3)

            print(f'{j}')
            table = driver.find_element_by_css_selector('.tbl_data')
            tr_list = table.find_elements_by_css_selector('tbody > tr')
            for tr in tr_list:
                df.loc[df_index,'shop'] = tr.find_element_by_css_selector('td:nth-child(2)').text
                df.loc[df_index,'1st'] = tr.find_element_by_css_selector('td:nth-child(3)').text
                df.loc[df_index,'location'] = tr.find_element_by_css_selector('td:nth-child(4)').text
                df_index+=1

            #페이지 끝날 시 저장
            print('save csv')
            df.to_csv(csv, encoding='utf-8')
    except Exception as e:
        #비정상 종료 시 저장
        print(e)
        df.to_csv(csv, encoding='utf-8')
    


    df.to_csv(csv, encoding='utf-8')
    driver.quit()
