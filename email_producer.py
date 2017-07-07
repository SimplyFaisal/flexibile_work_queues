import pika
import random
import time
from tutorial_pb2 import QueueMessage, EmailStudentTask, Student, Course


QUEUE = "EMAIL"

class EmailProducer():

    def __init__(self):
        self.connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        self.channel = self.connection.channel()
        self.channel.queue_declare(queue=QUEUE, durable=True)
        return

    def publish(self, message):
        self.channel.basic_publish(
            exchange='',
            routing_key=QUEUE,
            body=message.SerializeToString())
        return

if __name__ == '__main__':
    email_producer = EmailProducer();
    emails =['john@gmail.com', 'beth@gmail.com', 'alex@gmail.com']
    courses = ['Intro to Computing', 'Linear Algebra', 'History of Chairs']
    while True:
        email = random.choice(emails)
        course = random.choice(courses)

        task = EmailStudentTask(
            student=Student(email=email), course=Course(name=course))

        message = QueueMessage(
            type=QueueMessage.EMAIL_STUDENT_TASK,
            data=task.SerializeToString())
        print '[x] publishing {} {}'.format(email, course)
        email_producer.publish(message)
        time.sleep(3)


