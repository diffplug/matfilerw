% Generates UTF example data

%% generate a curly d (U+0221) in UTF 8, 16, and 32
val.utf8 = native2unicode([200 161],  'UTF-8');
val.utf16 = native2unicode([2 33],    'UTF-16');
val.utf32 = native2unicode([0 0 2 33],'UTF-32');

%% save it out to disk
save('utf.mat', 'val')
