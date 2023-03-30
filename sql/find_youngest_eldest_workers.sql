SELECT 'Youngest' as Type, name, birthday
FROM worker
WHERE birthday = (SELECT MAX(birthday) from worker)
UNION
SELECT 'Eldest' as Type, name, birthday
FROM worker
WHERE birthday = (SELECT MIN(birthday) from worker)
ORDER BY Type DESC, name;