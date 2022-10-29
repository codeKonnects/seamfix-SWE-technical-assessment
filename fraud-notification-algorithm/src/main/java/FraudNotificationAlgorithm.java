import java.util.*;
import java.util.stream.Collectors;
public class FraudNotificationAlgorithm {
    public static void main(String[] args) {
        int numberOfNotifications = numberOfNotifications(
                "9 5",
                "2 3 4 2 3 6 8 4 5"
        );
        int numberOfNotifications2 = numberOfNotifications(
                "4 2",
                "3 6 1 7 "
        );
        System.out.println("Number of notifications: " + numberOfNotifications);
        System.out.println("Number of notifications: " + numberOfNotifications2);
    }

    private static Integer numberOfNotifications(String transactionTrackingData, String dailyExpenditureData){
        List<Integer> transactionTrackingDataList = Arrays.stream(transactionTrackingData.split(" ")).map(Integer::parseInt).toList();
        List<Integer> dailyExpenditureDataList = Arrays.stream(dailyExpenditureData.split(" ")).map(Integer::parseInt).collect(Collectors.toList());
        int numberOfDays = transactionTrackingDataList.get(0);
        int numberOfDaysToTrack = transactionTrackingDataList.get(1);
        if (numberOfDays != dailyExpenditureDataList.size()) {
            throw new ArrayIndexOutOfBoundsException("data supplied for the number of days does not match the number of days supplied");
        }
        int numberOfNotifications = 0;
        for (int i = numberOfDaysToTrack; i < numberOfDays; i++) {
            int currentDayExpenditure = dailyExpenditureDataList.get(i);
            int medianStartDay = i - numberOfDaysToTrack;
            int medianEndDay = i - 1;
            int doubledMedianExpenditure = getTwoTimesMedianExpenditureForTrackedDays(dailyExpenditureDataList, medianStartDay, medianEndDay);
            if (currentDayExpenditure >= doubledMedianExpenditure) {
                numberOfNotifications++;
            }
        }
        return numberOfNotifications;

    }
    private static int getTwoTimesMedianExpenditureForTrackedDays(List<Integer> dailyExpenditureDataList, int firstDayData, int lastDayData) {
        List<Integer> pastDaysExpenditure = dailyExpenditureDataList.subList(firstDayData, lastDayData);
        Collections.sort(pastDaysExpenditure);
        int medianIndex = pastDaysExpenditure.size() / 2;
        int median = pastDaysExpenditure.get(medianIndex);
        if (pastDaysExpenditure.size() % 2 == 0) {
            median = (median + pastDaysExpenditure.get(medianIndex - 1)) / 2;
        }
        return median * 2;
    }

}
