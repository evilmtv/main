package seedu.task.storage;

import seedu.task.commons.core.LogsCenter;
import seedu.task.commons.exceptions.IllegalValueException;
import seedu.task.model.tag.Tag;
import seedu.task.model.tag.UniqueTagList;
import seedu.task.model.task.*;
import seedu.task.ui.TaskCard;

import javax.xml.bind.annotation.XmlElement;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * JAXB-friendly version of the Task.
 */
public class XmlAdaptedTask {
    private final Logger logger = LogsCenter.getLogger(XmlAdaptedTask.class);
    @XmlElement(required = true)
    private String name;
    @XmlElement(required = true)
    private String startTime;
    @XmlElement(required = true)
    private String endTime;
    @XmlElement(required = true)
    private String deadline;
    @XmlElement(required = true)
    private boolean doneStatus;
    @XmlElement(required = true)
    private boolean overdueStatus;
    @XmlElement(required = true)
    private boolean favoriteStatus;
    

    @XmlElement
    private List<XmlAdaptedTag> tagged = new ArrayList<>();

    /**
     * No-arg constructor for JAXB use.
     */
    public XmlAdaptedTask() {}


    /**
     * Converts a given Task into this class for JAXB use.
     *
     * @param source future changes to this will not affect the created XmlAdaptedTask
     */
    public XmlAdaptedTask(ReadOnlyTask source) {
        name = source.getName().fullName;
        startTime = source.getStartTime().value;
        endTime = source.getEndTime().value;
        deadline = source.getDeadline().value;
        
        
        doneStatus = source.getStatus().getDoneStatus();
        
        favoriteStatus = source.getStatus().getFavoriteStatus();
        tagged = new ArrayList<>();
        for (Tag tag : source.getTags()) {
            tagged.add(new XmlAdaptedTag(tag));
        }
    }

    /**
     * Converts this jaxb-friendly adapted task object into the model's Task object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted task
     */
    public Task toModelType() throws IllegalValueException {
        final List<Tag> taskTags = new ArrayList<>();
        for (XmlAdaptedTag tag : tagged) {
            taskTags.add(tag.toModelType());
        }
        final Name name = new Name(this.name);
        final StartTime startTime = new StartTime(this.startTime);
        final EndTime endTime = new EndTime(this.endTime);
        final Deadline deadline = new Deadline(this.deadline);
        final UniqueTagList tags = new UniqueTagList(taskTags);
        
        if (!this.deadline.isEmpty()) {
            String strDatewithTime = this.deadline.replace(" ", "T");
            LocalDateTime aLDT = LocalDateTime.parse(strDatewithTime);

            Date currentDate=new Date();
            LocalDateTime localDateTime = LocalDateTime.ofInstant(currentDate.toInstant(), ZoneId.systemDefault());
            
            logger.info("NOW IS " + localDateTime + "AND " + aLDT);
            
            
            if (aLDT.isBefore(localDateTime)) {
                this.overdueStatus = true;
            }
            else {
                this.overdueStatus = false;
            }
            
            }
            else {
                this.overdueStatus = false;
            }
        
        final Status status = new Status(this.doneStatus, this.overdueStatus, this.favoriteStatus);
        return new Task(name, startTime, endTime, deadline, tags, status);
    }
}