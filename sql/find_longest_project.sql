SELECT p.name, DATEDIFF(MONTH, p.start_date, p.finish_date) 
FROM project p, (SELECT MAX(DATEDIFF(MONTH, start_date, finish_date)) as max_diff FROM project) pp 
WHERE DATEDIFF(MONTH, p.start_date, p.finish_date) = pp.max_diff 
ORDER BY p.name;
