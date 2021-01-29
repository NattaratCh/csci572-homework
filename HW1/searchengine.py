from bs4 import BeautifulSoup
import time
import requests
from random import randint
import json
from time import sleep
from html.parser import HTMLParser
USER_AGENT = {'User-Agent':'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/61.0.3163.100 Safari/537.36'}

class SearchEngine:
    @staticmethod
    def search(query, sleep=True):
        if sleep:
            # Prevents loading too many pages too soon
            time.sleep(randint(10, 100))
        temp_url = '+'.join(query.split()) # for adding + between words for the query
        url = 'http://www.bing.com/search?q=' + temp_url + '&count=30'
        soup = BeautifulSoup(requests.get(url, headers=USER_AGENT).text, "html.parser")
        new_results = SearchEngine.scrape_search_result(soup)
        for url in new_results: print(url)
        return new_results

    @staticmethod
    def scrape_search_result(soup):
        raw_results = soup.find_all("li", {"class": "b_algo"})
        results = []
        convert_results = []
        # implement a check to get only 10 results and also check that URLs must not be duplicated
        for result in raw_results:
            link = result.find('a').get('href')
            url = SearchEngine.convert_url(link)
            if url not in convert_results:
                convert_results.append(url)
                results.append(link)
            if len(results) == 10:
                break
        return results

    @staticmethod
    def convert_url(url):
        # check last character
        last_char = url[-1]
        if last_char == '/':
            url = url[:-1]
        # convert to lowercase
        url = url.lower()

        url = url.replace('http://', '')
        url = url.replace('https://', '')
        url = url.replace('www.', '')
        return url


file = open('query.txt', 'r')
queries = file.readlines()
results = {}
for query in queries:
    print(query)
    top10 = SearchEngine.search(query.strip())
    results[query] = top10
file.close()

with open('hw1.json', 'w') as outfile:
    json.dump(results, outfile)
    outfile.close()

