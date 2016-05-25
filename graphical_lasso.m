function Graph = graphical_lasso(S,rho)
    dim = size(S);
    n = dim(1,1);
    p = dim(1,2);
    max_iterations = 100;
    tolerance = 1e-4;

    % Initialize W
    W = S + rho*eye(p);
    W_prev = W;

    for iter=1:max_iterations
        for j=p:-1:1
            i = j;

            W11 = W;
            W11(i,:) = [];  % remove ith row
            W11(:,j) = [];  % remove jth column
            w22 = W(i,j);

            s12 = S(:,j);
            s12(i,:) = [];
            
            A = W11^0.5;
            b = A\s12;
            
            cvx_begin quiet
                variable betaMatrix(p-1);
                %minimize ((0.5 * sum_square(A*betaMatrix - b)) + rho*norm(betaMatrix,1));
                minimize ((quad_form(betaMatrix, A'*A)/(2*rho)) - (2*b'*A*betaMatrix/(2*rho)) + norm(betaMatrix, 1))
            cvx_end
            w12 = W11 * betaMatrix;

            W = [W11(:,1:j-1) w12 W11(:,j:p-1)];

            w12_row = [w12(1:j-1) ; w22 ; w12(j:p-1)]';
            W = [W(1:i-1,:) ; w12_row ; W(i:p-1,:)];
        end
        if norm(W - W_prev) < tolerance
           break; 
        end
        W_prev = W;
    end
Theta = W^-1;

Graph = zeros(p,p);
for i=1:p
   for j=1:p
       if (Theta(i,j)>tolerance)
           Graph(i,j) = 1;
       end
   end 
end

for i=1:p
    Graph(i,i)=0;
end