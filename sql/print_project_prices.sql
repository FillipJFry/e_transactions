SELECT p.name, DATEDIFF(MONTH, p.start_date, p.finish_date) * SUM(w.salary) as Price
FROM project p INNER JOIN project_worker pw ON p.id = pw.project_id
               INNER JOIN worker w ON pw.worker_id = w.id
GROUP BY p.id
ORDER BY 2 DESC, 1;
