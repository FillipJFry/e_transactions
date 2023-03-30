SELECT c.name, COUNT(*) as PROJECT_COUNT
FROM client c INNER JOIN project p ON c.id = p.client_id
GROUP BY p.client_id
HAVING COUNT(*) = (
 SELECT MAX(proj_count) FROM (SELECT COUNT(*) as proj_count FROM project GROUP BY client_id) xyz)
ORDER BY 1;
