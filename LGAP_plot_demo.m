%Metric values randomly initialized for demo ,enter the values specific to an
%experiment to generate  plot for that experiment
%Also enter neighbourhood sizes after each (n =) in the Xlabel  specific
%to the experiment which are not initialized to anything (not necessary for
%demo)

sens(1)= 0.81;
spec(1)= 0.83;
prec(1)= 0.71;
gmean(1)= 0.82;
acc(1)= 0.83;

sens(2)= 0.62;
spec(2)= 0.92;
prec(2)= 0.82;
gmean(2)= 0.76;
acc(2)= 0.78;

sens(3)= 0.62;
spec(3)= 0.92;
prec(3)= 0.82;
gmean(3)= 0.76;
acc(3)= 0.78;

sens(4)= 0.62;
spec(4)= 0.92;
prec(4)= 0.82;
gmean(4)= 0.76;
acc(4)= 0.78;

sens(5)= 0.62;
spec(5)= 0.42;
prec(5)= 0.12;
gmean(5)= 0.36;
acc(5)= 0.18;

%Bar plot
y = [sens(1) spec(1) prec(1) gmean(1) acc(1);sens(2) spec(2) prec(2) gmean(2) acc(2);sens(3) spec(3) prec(3) gmean(3) acc(3);sens(4) spec(4) prec(4) gmean(4) acc(4);sens(5) spec(5) prec(5) gmean(5) acc(5)];

bar(y)
ylim([0 1])
set(gca,'XTickLabel',{'n=', 'n=', 'n=', 'n=', 'n='});
Xlabel('Neighbourhood Size');
legend('sensitivity','specificity','precision','gmean','accuracy')
Title('Demo LGAP for random metric values')

