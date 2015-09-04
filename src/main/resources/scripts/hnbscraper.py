#!/usr/bin/python
"""
This script scrapes the hnb site for exchange rates of USD, CHF, EUR, JPY on a specific date
"""

from urllib2 import urlopen, HTTPError
import argparse

BASE_URL = "http://www.hnb.hr/tecajn/f{}.dat"
CURRENCIES = ["USD", "EUR", "CHF", "JPY"]

def get_rates(url):
    try:
        html = urlopen(url).read()
    except HTTPError, error:
        html = error
        return None
    return html

def parse_rates(rates):
    derived_rates = []
    lines = rates.splitlines()

    date = lines[0][11:19]

    for line in lines[1:]:
        columns = line.split()
        name = columns[0][3:6]
        if name not in CURRENCIES:
            continue
        rate = columns[2]
        unit = columns[0][6:9]
        exchange_rate = [name, unit, rate, date]
        derived_rates.append(exchange_rate)
    return derived_rates

def run_scraper(dateString):
    url = BASE_URL.format(dateString)
    rates = get_rates(url)
    if rates is not None:
        return parse_rates(rates)

def main(date):
    return run_scraper(date)

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Retrieves the exchange rates for USD, CHF, EUR, JPY on a specific date from the http://www.hnb.hr/tecajn .")
    parser.add_argument('date', metavar='DATE', type=str, help='date of the exchange rate')
    args = parser.parse_args()
    print main(args.date)


