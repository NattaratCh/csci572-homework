import json
import csv


class Calculation:

    @staticmethod
    def load_data(filename):
        with open(filename, 'r') as json_file:
            data = json.load(json_file)
        json_file.close()
        return data

    @staticmethod
    def load_query():
        file = open('query.txt', 'r')
        data = file.readlines()
        result = []
        for query in data:
            result.append(query.strip().strip('\n'))
        file.close()
        return result

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

    @staticmethod
    def calculate_rho(google_lst, bing_lst):
        n = 0
        sum_of_d_square = 0
        for gg_rank in range(0, len(google_lst)):
            gg_link = google_lst[gg_rank]
            try:
                bing_rank = bing_lst.index(gg_link)
                d = gg_rank - bing_rank
                n += 1
                sum_of_d_square += d*d
            except ValueError:
                print(gg_link, 'is not found on bing result')

        number_of_overlapping = n
        percent_overlap = n/10*100

        if n == 0:
            rho = 0
        elif n == 1:
            if sum_of_d_square == 0:
                rho = 1
            else:
                rho = 0
        else:
            rho = 1 - ((6 * sum_of_d_square)/(n * (n*n -1)))

        return number_of_overlapping, percent_overlap, rho


google_results = Calculation.load_data('google.json')
bing_results = Calculation.load_data('hw1.json')
queries = Calculation.load_query()

sum_number_of_overlapping = 0
sum_percent_overlap = 0
sum_rho = 0
csv_results = {}

for query in queries:
    print(query)
    result1 = google_results[query]
    convert_result1 = []
    for r in result1: convert_result1.append(Calculation.convert_url(r))

    result2 = bing_results[query]
    convert_result2 = []
    for r in result2: convert_result2.append(Calculation.convert_url(r))

    number_of_overlapping, percent_overlap, rho = Calculation.calculate_rho(convert_result1, convert_result2)
    print(number_of_overlapping, percent_overlap, rho)
    sum_number_of_overlapping += number_of_overlapping
    sum_percent_overlap += percent_overlap
    sum_rho += rho

    csv_results[query] = []
    csv_results[query] = {
        'number_of_overlapping': number_of_overlapping,
        'percent_overlap': percent_overlap,
        'rho': rho
    }

print(csv_results)

avg_number_of_overlapping = sum_number_of_overlapping/len(query)
avg_percent_overlap = sum_percent_overlap/len(query)
avg_rho = sum_rho/len(query)

with open('hw1.csv', 'w', newline='') as csv_file:
    writer = csv.writer(csv_file, delimiter='\t')
    rows = []
    rows.append(['Queries,', 'Number of Overlapping Results,', 'Percent Overlap,', 'Spearman Coefficient'])
    for query in queries:
        data = csv_results[query]
        rows.append([query + ',', str(data['number_of_overlapping']) + ',', str(data['percent_overlap']) + ',', str(data['rho'])])
    rows.append(['Averages,', str(avg_number_of_overlapping) + ',', str(avg_percent_overlap) + ',', str(avg_rho)])
    writer.writerows(rows)
    csv_file.close()


