##Here is the link to the heroku deployment.

###https://group12-hw6.herokuapp.com

We couldn’t get the data base to display despite looking at error logs, trying to inject raw sql, checking the database connection and terminal output that indicated that the database was connected:


Hannahs-MacBook-Pro-2:HW 6 hannahlynn$ heroku pg:psql -a group12-hw6


roup12-hw6::DATABASE=> \dt


List of relations



| Schema |   Name    | Type  |     Owner
|--------|-----------|-------|----------------
public | employers | table | mqsahtfubqyeih
(1 row)


group12-hw6::DATABASE=> select * from employers;


|id |     name      |   sector   | summary
|----|---------------|------------|---------
1 | Apple         | technology |
2 | Johns Hopkins | University |
3 | JP Morgan     | Finance    |

(3 rows)



Hannahs-MacBook-Pro-2:HW 6 hannahlynn$ heroku pg:info -a group12-hw6
=== DATABASE_URL
Plan:                  Hobby-dev


Status:                Available


Connections:           1/20


PG Version:            13.4


Created:               2021-10-15 14:47 UTC


Data Size:             8.1 MB/1.00 GB (In compliance)


Tables:                1


Rows:                  3/10000 (In compliance)


Fork/Follow:           Unsupported


Rollback:              Unsupported


Continuous Protection: Off


Add-on:                postgresql-concentric-60091