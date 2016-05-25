%Metric values randomly initialized for demo ,enter the values specific to an
%experiment to generate  plot for that experiment

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

%Bar plot
y = [sens(1) spec(1) prec(1) gmean(1) acc(1);sens(2) spec(2) prec(2) gmean(2) acc(2);sens(3) spec(3) prec(3) gmean(3) acc(3)];

bar(y)
ylim([0 1])
set(gca,'XTickLabel',{'linear', 'Poly', 'rbf'});
Xlabel('kernel');
legend('sensitivity','specificity','precision','gmean','accuracy')
Title('Demo OSVM for random metric values')

