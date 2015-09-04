from datetime import datetime, date, timedelta
from dateutil.rrule import rrule, DAILY
import hnbscraper
import psycopg2
import sys



def scrape_last_year():
    results = ()
    end = datetime.now()
    delta = timedelta(days=365)
    start = end - delta
    print end, start

    for dt in rrule(DAILY, dtstart=start, until=end):
        result = hnbscraper.main(dt.strftime("%d%m%y"))
        if result is not None:
            tuple_result = []
            for res in result:
                res[2] = float(res[2].replace(",", "."))
                res[3] = format_pgsql_date(res[3])
                tuple_result.append(tuple(res))

            results = results + tuple(tuple_result)
    return results

def insert_db(data):
    con = None
    try:
        con = psycopg2.connect("dbname='hnbapp' user='hnbapp'")
        cur = con.cursor()
        query = "INSERT INTO currency (name, unit, rate, date) VALUES (%s, %s, %s, %s)"
        cur.executemany(query, data)
        print data

        con.commit()
    except psycopg2.DatabaseError, e:
        if con:
            con.rollback()

        print 'Error %s' % e
        sys.exit(1)
    finally:
        if con:
            con.close()

def format_pgsql_date(date):
    return "{}-{}-{}".format(date[4:], date[2:4], date[:2])

data = scrape_last_year()
insert_db(data)
