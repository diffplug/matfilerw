

% determine if script is being run from maven style project directory
% if so, set root dir approriately, otherwise just use current directory
% in maven directory structure this file should be located in directory
% project_root/src/test/matlab/com/jmatio/TestMultiDimArray.m
proj_root = '../../../../../'
test_rsrc_dir = 'src/test/resources/'

rsrc_dir = strcat( proj_root, test_rsrc_dir )

fnMultiDimMatrix = 'multiDimMatrix.mat'

% by default, use current directory as root
test_dir = './';
if exist( rsrc_dir, 'dir' )
    test_dir = rsrc_dir;
end

genMultiDimMatrix(strcat( test_dir, fnMultiDimMatrix ))




function op = genMultiDimMatrix( filePath )
    in = zeros(2, 3, 4, 5, 6);
    e = 0;
    for i =1:6
        for j = 1:5
            for k = 1:4
                for l = 1:3 
                    for m = 1:2
                        in(m, l, k, j, i) = e;
                        e = e + 1;
                    end
                end
            end
        end
    end
                        
    
    
    
    save( filePath, 'in' )
end

